package com.easylancer.api.controllers

import com.easylancer.api.controllers.data.ImagesUpdate
import com.easylancer.api.data.EventEmitter
import com.easylancer.api.data.DataApiClient
import com.easylancer.api.data.dto.types.TaskStatus
import com.easylancer.api.data.exceptions.DataApiBadRequestException
import com.easylancer.api.data.exceptions.DataApiNotFoundException
import com.easylancer.api.data.exceptions.DataApiConflictException
import com.easylancer.api.dto.*
import com.easylancer.api.exceptions.http.HttpBadRequestException
import com.easylancer.api.exceptions.http.HttpConflictException
import com.easylancer.api.exceptions.http.HttpNotFoundException
import com.easylancer.api.files.FilesApiBadRequestException
import com.easylancer.api.files.FilesApiClient
import com.easylancer.api.helpers.toJson
import com.easylancer.api.security.UserPrincipal
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import reactor.core.publisher.onErrorMap

import org.bson.types.ObjectId
import javax.validation.Valid

@RestController
@RequestMapping("/tasks")
class TaskPageController(
        @Autowired val eventEmitter: EventEmitter,
        @Autowired private val client: DataApiClient,
        @Autowired private val files: FilesApiClient
) {
    private val mapper: ObjectMapper = jacksonObjectMapper()

    @PostMapping("/create")
    fun createTask(
            @RequestBody taskDto: CreateTaskDTO,
            @AuthenticationPrincipal user: UserPrincipal
    ) : Mono<ListViewTaskDTO> {
        val taskBody = taskDto.toJson()
                .put("creatorUser", user.id.toHexString())

        return (
            files.check(taskDto.imagesUrls.toList())
                .then(client.postTask(taskBody))
                .doOnSuccess {
                    eventEmitter.filesUsed(taskDto.imagesUrls.toList())
                }
                .map { task ->
                    task.toListViewTaskDTO()
                }.onErrorMap(DataApiBadRequestException::class) { e ->
                    HttpBadRequestException("Invalid task data", e, e.invalidParams)
                }.onErrorMap(FilesApiBadRequestException::class) { e ->
                    HttpBadRequestException("Invalid files data", e)
                }
        )
    }

    @GetMapping("/{id}/view")
    fun viewTask(
            @PathVariable("id") id: ObjectId,
            @AuthenticationPrincipal user: UserPrincipal
    ) : Mono<DetailViewTaskDTO> {
        return client.getFullTask(id).doOnSuccess {
            eventEmitter.taskSeenByUser(id, user.id)
        }.map { task ->
            if(task.creatorUser._id == user.id) {
                task.toOwnerViewTaskDTO()
            } else if (task.workerUser != null && task.workerUser._id == user.id) {
                task.toWorkerViewTaskDTO()
            } else {
                task.toViewerViewTaskDTO()
            }
        }.onErrorMap(DataApiNotFoundException::class) { e ->
            HttpNotFoundException("No task found with this id", e)
        }
    }

    @GetMapping("/{id}/offers")
    fun viewTaskOffers(
            @PathVariable("id") id: ObjectId,
            @AuthenticationPrincipal user: UserPrincipal
    ) : Mono<List<ViewOfferDTO>> {
        return client.getTask(id)
                .zipWith(client.getTaskOffers(id).collectList())
                .flatMapIterable { tuple ->
                    val isOwner = tuple.t1.creatorUser == user.id

                    tuple.t2.map { offer ->
                        offer.toViewOfferDTO()
                    }.filter { offer ->
                        isOwner || offer.workerUser.id == user.id.toHexString()
                    }
                }.collectList()
    }

    @PostMapping("/{id}/apply")
    fun applyToTask(
            @PathVariable("id") id: String,
            @RequestBody offerDto: CreateOfferDTO,
            @AuthenticationPrincipal user: UserPrincipal
    ) : Mono<IdViewDTO> {
        return client.postOffer(
                offerDto.toJson()
                        .put("task", id)
                        .put("workerUser", user.id.toHexString())
        ).map { offer ->
            offer.toIdDTO()
        }.onErrorMap(DataApiConflictException::class) { e ->
            HttpConflictException("Task is closed for offers or an offer was already made", e)
        }.onErrorMap(DataApiBadRequestException::class) { e ->
            HttpBadRequestException("Sorry can't do, please send a valid offer data!", e, e.invalidParams)
        }
    }

    @PostMapping("/{id}/withdraw")
    @PreAuthorize("hasAuthority('task:applied:' + #id)")
    fun withdrawTaskOffer(
            @PathVariable("id") id: ObjectId,
            @AuthenticationPrincipal user: UserPrincipal
    ) : Mono<IdViewDTO> {
        val query = HashMap<String, List<String>>()

        query["task"] = listOf(id.toHexString())
        query["workerUser"] = listOf(user.id.toHexString())

        return client.findOneOffer(query).flatMap { offer ->
            client.deleteOffer(offer._id)
        }.map { offer ->
            offer.toIdDTO()
        }.onErrorMap(DataApiConflictException::class) { e ->
            HttpConflictException("Offer is already accepted", e)
        }.onErrorMap(DataApiNotFoundException::class) { e ->
            HttpNotFoundException("Offer is already removed", e)
        }
    }

    @PostMapping("/{id}/edit")
    @PreAuthorize("hasAuthority('task:owner:' + #id)")
    fun updateTask(
            @PathVariable("id") id: ObjectId,
            @RequestBody taskDto: UpdateTaskDTO,
            @AuthenticationPrincipal user: UserPrincipal
    ) : Mono<ListViewTaskDTO> {
        val getImagesToUpdateMono = (
            if (taskDto.imagesUrls != null) {
                client.getTask(id).map { oldTask ->
                    ImagesUpdate(
                        taskDto.imagesUrls.toList().minus(oldTask.imagesUrls),
                        oldTask.imagesUrls.toList().minus(taskDto.imagesUrls)
                    )
                }.doOnSuccess {
                    files.check(it.added).subscribe()
                }
            } else {
                Mono.empty()
            }
        );

        val updateImages = { images: ImagesUpdate ->
            if (taskDto.imagesUrls != null) {
                eventEmitter.filesUsed(images.added)
                eventEmitter.filesRemoved(images.removed)
            }
        }

        return (
            getImagesToUpdateMono.flatMap { images ->
                client.putTask(id, taskDto).doOnSuccess{
                    updateImages(images)
                }
            }.doOnNext {
                val query = HashMap<String, List<String>>()

                query["task"] = listOf(id.toHexString())

                client.deleteOffers(query).subscribe()
            }.map { task ->
                task.toListViewTaskDTO()
            }.onErrorMap(DataApiBadRequestException::class) { e ->
                HttpBadRequestException("Sorry can't do, please send a valid task data change!", e, e.invalidParams)
            }.onErrorMap(DataApiConflictException::class) { e ->
                HttpConflictException("Cannot update this task", e)
            }
        )
    }

    @PostMapping("/{id}/accept")
    @PreAuthorize("hasAuthority('task:owner:' + #id)")
    fun acceptOfferToTask(
            @PathVariable("id") id: ObjectId,
            @Valid @RequestBody offerDto: AcceptOfferDTO,
            @AuthenticationPrincipal user: UserPrincipal
    ) : Mono<ListViewTaskDTO> {
        val body = mapper.createObjectNode()
                .put("acceptedOffer", offerDto.id);

        return client.putTask(id, body).map { task ->
            task.toListViewTaskDTO()
        }.onErrorMap(DataApiConflictException::class) { e ->
            HttpConflictException("Task already assigned", e)
        }
    }

    @PostMapping("/{id}/start")
    @PreAuthorize("hasAuthority('task:worker:' + #id)")
    fun startTask(
            @PathVariable("id") id: ObjectId,
            @AuthenticationPrincipal user: UserPrincipal
    ) : Mono<ListViewTaskDTO> {
        val body = mapper.createObjectNode()
                .put("status", TaskStatus.IN_PROGRESS.toString());

        return client.putTask(id, body).map { task ->
            task.toListViewTaskDTO()
        }.onErrorMap(DataApiConflictException::class) { e ->
            HttpConflictException("Task cannot be started", e)
        }
    }

    @PostMapping("/{id}/review/worker")
    @PreAuthorize("hasAuthority('task:worker:' + #id)")
    fun reviewTaskByWorker(
            @PathVariable("id") id: ObjectId,
            @RequestBody reviewDto: CreateTaskReviewDTO,
            @AuthenticationPrincipal user: UserPrincipal
    ): Mono<ListViewTaskDTO> {
        val body = mapper.createObjectNode()
                .set("workerRating", reviewDto.toJson());

        return client.putTask(id, body).map { task ->
            task.toListViewTaskDTO()
        }.onErrorMap(DataApiBadRequestException::class) { e ->
            HttpBadRequestException("Sorry can't do, please send a valid review data!", e, e.invalidParams)
        }.onErrorMap(DataApiConflictException::class) { e ->
            HttpConflictException("Cannot add a review just yet!", e)
        }
    }

    @PostMapping("/{id}/review/owner")
    @PreAuthorize("hasAuthority('task:owner:' + #id)")
    fun reviewTaskByOwner(
            @PathVariable("id") id: ObjectId,
            @RequestBody reviewDto: CreateTaskReviewDTO,
            @AuthenticationPrincipal user: UserPrincipal
    ): Mono<ListViewTaskDTO> {
        val body = mapper.createObjectNode()
                .set("creatorRating", reviewDto.toJson());

        return client.putTask(id, body).map { task ->
            task.toListViewTaskDTO()
        }.onErrorMap(DataApiBadRequestException::class) { e ->
            HttpBadRequestException("Sorry can't do, please send a valid review data!", e, e.invalidParams)
        }.onErrorMap(DataApiConflictException::class) { e ->
            HttpConflictException("Cannot add a review just yet!", e)
        }
    }

    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasAuthority('task:owner:' + #id) || hasAuthority('task:worker:' + #id)")
    fun cancelTask(
            @PathVariable("id") id: ObjectId,
            @AuthenticationPrincipal user: UserPrincipal
    ) : Mono<IdViewDTO> {
        val body = mapper.createObjectNode()
                .put("status", TaskStatus.CANCELLED.toString());

        return client.putTask(id, body).map { task ->
            task.toIdDTO()
        }.onErrorMap(DataApiConflictException::class) { e ->
            HttpConflictException("Task cannot be cancelled now", e)
        }.onErrorMap(DataApiNotFoundException::class) { e ->
            HttpNotFoundException("Task not found", e)
        }
    }
}