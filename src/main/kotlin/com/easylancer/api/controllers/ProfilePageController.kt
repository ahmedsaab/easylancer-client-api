package com.easylancer.api.controllers

import com.easylancer.api.controllers.data.ImagesUpdate
import com.easylancer.api.data.DataApiClient
import com.easylancer.api.data.EventEmitter
import com.easylancer.api.data.dto.inbound.FullTaskDTO
import com.easylancer.api.data.dto.outbound.EqualFilter
import com.easylancer.api.data.dto.outbound.Query
import com.easylancer.api.data.dto.types.TaskStatus
import com.easylancer.api.data.exceptions.DataApiBadRequestException
import com.easylancer.api.data.exceptions.DataApiNotFoundException
import com.easylancer.api.dto.*
import com.easylancer.api.exceptions.http.HttpBadRequestException
import com.easylancer.api.exceptions.http.HttpNotFoundException
import com.easylancer.api.files.FilesApiClient
import com.easylancer.api.security.UserPrincipal
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.bson.types.ObjectId

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import reactor.core.publisher.onErrorMap

@RequestMapping("/profiles")
@RestController
class ProfilePageController(
        @Autowired private val client: DataApiClient,
        @Autowired private val files: FilesApiClient,
        @Autowired val eventEmitter: EventEmitter
) {
    private val mapper: ObjectMapper = jacksonObjectMapper()

    @GetMapping("/{id}/view")
    fun viewProfile(
            @PathVariable("id") id: ObjectId
    ) : Mono<ViewProfileDTO> {
        return client.getUser(id).map {
            it.toViewProfileDTO()
        }.onErrorMap(DataApiNotFoundException::class) { e ->
            HttpNotFoundException("no user found with this id", e)
        }
    }

    @PostMapping("/{id}/edit")
    @PreAuthorize("hasAuthority('user:edit:' + #id)")
    fun updateProfile(
            @PathVariable("id") id: ObjectId,
            @RequestBody profileDto: UpdateProfileDTO,
            @AuthenticationPrincipal user: UserPrincipal
    ) : Mono<ViewProfileDTO> {
        val getImagesToUpdateMono = (
            if (profileDto.imagesUrls != null || profileDto.imageUrl != null) {
                client.getUser(id).map { oldUser ->
                    val images = ImagesUpdate()

                    if(profileDto.imagesUrls !== null) {
                        images.added = profileDto.imagesUrls.toList().minus(oldUser.imagesUrls)
                        images.removed = oldUser.imagesUrls.toList().minus(profileDto.imagesUrls)
                    }
                    if(profileDto.imageUrl !== null) {
                        images.added = images.added.plus(profileDto.imageUrl)
                        // TODO: check if imageUrl is not from the default avatar urls
                        if (oldUser.imageUrl !== null && false) {
                            images.removed = images.removed.plus(oldUser.imageUrl)
                        }
                    }

                    images
                }.doOnSuccess {
                    files.check(it.added).subscribe()
                }
            } else {
                Mono.empty()
            }
        )

        val updateImages = { images: ImagesUpdate ->
            if (profileDto.imagesUrls != null || profileDto.imageUrl != null) {
                eventEmitter.filesUsed(images.added)
                eventEmitter.filesRemoved(images.removed)
            }
        }

        return getImagesToUpdateMono.flatMap { images ->
            client.putUser(id, profileDto).doOnSuccess{
                updateImages(images)
            }
        }.map {
            it.toViewProfileDTO()
        }.onErrorMap(DataApiBadRequestException::class) { e ->
            HttpBadRequestException("Sorry can't do, please send a valid profile data change!", e, e.invalidParams)
        }
    }

    @GetMapping("/{id}/tasks/finished")
    fun listUserAssignedTasks(
            @PathVariable("id") id: ObjectId
    ) : Mono<List<ListViewTaskDTO>> {
        return client.getUserFinishedTasks(id).map {
            it.toListViewTaskDTO()
        }.collectList()
    }

    @GetMapping("/{id}/tasks/created")
    fun listUserCreatedTasks(
            @PathVariable("id") id: ObjectId
    ): Mono<List<ListViewTaskDTO>> {
        return client.getUserCreatedTasks(id).map {
            it.toListViewTaskDTO()
        }.collectList()
    }

    @GetMapping("/{id}/worker/reviews")
    fun getUserWorkerReviews(
            @RequestParam("page") pageNo: Int = 1,
            @RequestParam("status") status: TaskStatus,
            @PathVariable("id") id: ObjectId
    ): Mono<PaginationViewDTO<DetailViewTaskDTO>> {
        val query = Query()
            .filter("status",  EqualFilter(status))
            .filter("workerUser", EqualFilter(id.toHexString()))

        return client.searchTasks(query, pageNo - 1, 10).map { pagination ->
            if(pagination.pageNo != 0 && pagination.page.isEmpty()) {
                throw HttpNotFoundException("page not found")
            }
            pagination.transform {
                task -> task.toViewerViewTaskDTO()
            }
        }.onErrorMap(DataApiBadRequestException::class) { e ->
            HttpBadRequestException("Invalid 'page' param, 'page' must be an integer not be less than 1", e)
        }
    }

    @GetMapping("/{id}/owner/reviews")
    fun getUserOwnerReviews(
            @RequestParam("page") pageNo: Int = 1,
            @RequestParam("status") status: TaskStatus,
            @PathVariable("id") id: ObjectId
    ): Mono<PaginationViewDTO<DetailViewTaskDTO>> {
        val query = Query()
                .filter("status",  EqualFilter(status))
                .filter("creatorUser", EqualFilter(id.toHexString()))

        return client.searchTasks(query, pageNo - 1, 5).map { pagination ->
            if(pagination.pageNo != 0 && pagination.page.isEmpty()) {
                throw HttpNotFoundException("page not found")
            }
            pagination.transform {
                task -> task.toViewerViewTaskDTO()
            }
        }.onErrorMap(DataApiBadRequestException::class) { e ->
            HttpBadRequestException("Invalid 'page' param, 'page' must be an integer not be less than 1", e)
        }
    }

    // TODO: this is postponed after the release of 1.0.0
    @PostMapping("/{id}/approve")
    fun approveUser(
            @PathVariable("id") id: ObjectId
    ): Unit {

    }
}