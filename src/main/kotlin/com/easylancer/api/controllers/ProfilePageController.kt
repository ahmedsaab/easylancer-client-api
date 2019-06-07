package com.easylancer.api.controllers

import com.easylancer.api.data.DataApiClient
import com.easylancer.api.data.exceptions.DataApiBadRequestException
import com.easylancer.api.data.exceptions.DataApiNotFoundException
import com.easylancer.api.dto.*
import com.easylancer.api.exceptions.http.HttpBadRequestException
import com.easylancer.api.exceptions.http.HttpNotFoundException
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
        @Autowired private val client: DataApiClient
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

    @PutMapping("/{id}/edit")
    @PreAuthorize("hasAuthority('user:edit:' + #id)")
    fun updateProfile(
            @PathVariable("id") id: ObjectId,
            @RequestBody profileDto: UpdateProfileDTO,
            @AuthenticationPrincipal user: UserPrincipal
    ) : Mono<ViewProfileDTO> {
        return client.putUser(id, profileDto).map {
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

    @GetMapping("/{id}/reviews")
    fun listUserTaskReviews(
            @PathVariable("id") id: ObjectId
    ): Mono<List<ListViewTaskRatingDTO>> {
        return client.getUserReviews(id).map {
            it.toListViewTaskRatingDTO()
        }.collectList();
    }

    // TODO: this is postponed after the release of 1.0.0
    @PostMapping("/{id}/approve")
    fun approveUser(
            @PathVariable("id") id: ObjectId
    ): Unit {

    }
}