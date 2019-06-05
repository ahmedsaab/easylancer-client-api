package com.easylancer.api.controllers

import com.easylancer.api.data.EventEmitter
import com.easylancer.api.data.DataApiClient
import com.easylancer.api.data.exceptions.DataApiBadRequestException
import com.easylancer.api.data.exceptions.DataApiNotFoundException
import com.easylancer.api.dto.*
import com.easylancer.api.exceptions.http.HttpBadRequestException
import com.easylancer.api.exceptions.http.HttpNotFoundException
import com.easylancer.api.helpers.toJson
import com.easylancer.api.security.UserPrincipal
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@CrossOrigin()
@RequestMapping("/tasks")
class TaskPageController(
        @Autowired val eventEmitter: EventEmitter,
        @Autowired private val client: DataApiClient
) {
    private val mapper: ObjectMapper = jacksonObjectMapper()

    @PostMapping("/create")
    fun createTask(
            @RequestBody taskDto: CreateTaskDTO,
            @AuthenticationPrincipal user: UserPrincipal
    ) : Mono<ListViewTaskDTO> {
        val taskBody = taskDto.toJson()
                .put("creatorUser", user.id)

        return client.postTask(taskBody).map { task ->
            task.toListViewTaskDTO()
        }.onErrorMap { e ->
            if (e is DataApiBadRequestException)
                HttpBadRequestException("Sorry can't do, please send a valid task data!", e, e.invalidParams)
            else e
        }
    }

    @GetMapping("/{id}/view")
    fun viewTask(
            @PathVariable("id") id: String,
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
        }.onErrorMap { e ->
            if(e is DataApiNotFoundException) {
                HttpNotFoundException("No task found with this id", e)
            } else {
                e
            }
        }
    }

    @GetMapping("/{id}/offers")
    fun viewTaskOffers(
            @PathVariable("id") id: String,
            @AuthenticationPrincipal user: UserPrincipal
    ) : Flux<ViewOfferDTO> {
        return client.getTask(id)
                .zipWith(client.getTaskOffers(id).collectList())
                .flatMapIterable { tuple ->
                    val isOwner = tuple.t1.creatorUser == user.id

                    tuple.t2.map { offer ->
                        offer.toViewOfferDTO()
                    }.filter { offer ->
                        isOwner || offer.workerUser.id == user.id
                    }
                }
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
                        .put("workerUser", user.id)
        ).map { offer ->
            offer.toIdDTO()
        }
    }

    @PutMapping("/{id}/edit")
    @PreAuthorize("hasAuthority('task:owner:' + #id)")
    fun updateTask(
            @PathVariable("id") id: String,
            @RequestBody taskDto: UpdateTaskDTO,
            @AuthenticationPrincipal user: UserPrincipal
    ) : Mono<ListViewTaskDTO> {
        return client.putTask(id, taskDto).map { task ->
            task.toListViewTaskDTO()
        }
    }

    @PostMapping("/{id}/accept")
    @PreAuthorize("hasAuthority('task:owner:' + #id)")
    fun acceptOfferToTask(
            @PathVariable("id") id: String,
            @RequestBody offerDto: AcceptOfferDTO,
            @AuthenticationPrincipal user: UserPrincipal
    ) : Mono<ListViewTaskDTO> {
        val body = mapper.createObjectNode()
                .put("acceptedOffer", offerDto.id);

        return client.putTask(id, body).map { task ->
            task.toListViewTaskDTO()
        }
    }

    @PostMapping("/{id}/start")
    @PreAuthorize("hasAuthority('task:worker:' + #id)")
    fun startTask(
            @PathVariable("id") id: String,
            @AuthenticationPrincipal user: UserPrincipal
    ) : Mono<ListViewTaskDTO> {
        val body = mapper.createObjectNode()
                .put("status", "in-progress");

        return client.putTask(id, body).map { task ->
            task.toListViewTaskDTO()
        }
    }

    @PostMapping("/{id}/review/worker")
    @PreAuthorize("hasAuthority('task:worker:' + #id)")
    fun reviewTaskByWorker(
            @PathVariable("id") id: String,
            @RequestBody reviewDto: CreateTaskReviewDTO,
            @AuthenticationPrincipal user: UserPrincipal
    ): Mono<ListViewTaskDTO> {
        val body = mapper.createObjectNode()
                .set("workerRating", reviewDto.toJson());

        return client.putTask(id, body).map { task ->
            task.toListViewTaskDTO()
        }
    }

    @PostMapping("/{id}/review/owner")
    @PreAuthorize("hasAuthority('task:owner:' + #id)")
    fun reviewTaskByOwner(
            @PathVariable("id") id: String,
            @RequestBody reviewDto: CreateTaskReviewDTO,
            @AuthenticationPrincipal user: UserPrincipal
    ): Mono<ListViewTaskDTO> {
        val body = mapper.createObjectNode()
                .set("creatorRating", reviewDto.toJson());

        return client.putTask(id, body).map { task ->
            task.toListViewTaskDTO()
        }
    }

    // TODO: this is postponed after the release of 0.9.0
    @PostMapping("/{id}/cancel")
    suspend fun cancelTask(@RequestBody taskBody: CreateTaskDTO) : Unit {


    }
}