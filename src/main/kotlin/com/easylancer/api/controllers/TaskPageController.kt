package com.easylancer.api.controllers

import com.easylancer.api.data.EventEmitter
import com.easylancer.api.data.dto.FullTaskDTO
import com.easylancer.api.data.dto.TaskDTO
import com.easylancer.api.data.blocking.exceptions.DataApiBadRequestException
import com.easylancer.api.data.blocking.exceptions.DataApiResponseException
import com.easylancer.api.dto.*
import com.easylancer.api.exceptions.http.HttpAuthorizationException
import com.easylancer.api.exceptions.http.HttpBadRequestException
import com.easylancer.api.exceptions.http.HttpNotFoundException
import com.easylancer.api.security.User
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import kotlinx.coroutines.*

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/tasks")
class TaskPageController(
        @Autowired val eventEmitter: EventEmitter,
        @Autowired private val bClient: com.easylancer.api.data.blocking.DataApiClient
) {
    private val mapper: ObjectMapper = jacksonObjectMapper()

    @PostMapping("/create")
    suspend fun createTask(
            @RequestBody taskDto: CreateTaskDTO,
            @AuthenticationPrincipal user: User
    ) : IdViewDTO {
        try {
            val taskBody = mapper.valueToTree<ObjectNode>(taskDto)
                    .put("creatorUser", user.id)
            val task: TaskDTO = bClient.postTask(taskBody)

            return task.toIdDTO();
        } catch(e: DataApiBadRequestException) {
            throw HttpBadRequestException("Invalid task parameters", e, e.invalidParams)
        }
    }

    @GetMapping("/{id}/view")
    suspend fun viewTask(
            @PathVariable("id") id: String,
            @AuthenticationPrincipal oAuth2User: OAuth2User
    ) : DetailViewTaskDTO {
        try {
            val task: FullTaskDTO = bClient.getFullTask(id)

            eventEmitter.taskSeenByUser(id, oAuth2User.name)

            return if(task.creatorUser._id == oAuth2User.name) {
                task.toOwnerViewTaskDTO()
            } else if (task.workerUser != null && task.workerUser._id == oAuth2User.name){
                task.toWorkerViewTaskDTO()
            } else {
                task.toViewerViewTaskDTO()
            }
        } catch (e: DataApiResponseException) {
            if(e.response.statusCode == HttpStatus.NOT_FOUND.value()) {
                throw HttpNotFoundException("Task not found")
            }
            throw e
        }

    }

    @GetMapping("/{id}/offers")
    suspend fun viewTaskOffers(
            @PathVariable("id") id: String,
            @AuthenticationPrincipal user: User
    ) : List<ViewOfferDTO> = coroutineScope {
        val offersAsync = async { bClient.getTaskOffers(id) }
        val taskAsync = async { bClient.getTask(id) }

        offersAsync.await().filter {
            it.workerUser._id == user.id || taskAsync.await().creatorUser == user.id
        }.map {
            it.toViewOfferDTO()
        }
    }

    @PutMapping("/{id}/edit")
    suspend fun updateTask(
            @PathVariable("id") id: String,
            @RequestBody taskDto: UpdateTaskDTO,
            @AuthenticationPrincipal user: User
    ) : IdViewDTO {
        val taskBody = mapper.valueToTree<ObjectNode>(taskDto)
        val task = bClient.getTask(id)

        if(task.creatorUser == user.id) {
            bClient.putTask(id, taskBody)
        } else {
            throw HttpAuthorizationException("Cannot update this task")
        }

        return IdViewDTO(id);
    }

    @PostMapping("/{id}/apply")
    suspend fun applyToTask(
            @PathVariable("id") id: String,
            @RequestBody offerDto: CreateOfferDTO,
            @AuthenticationPrincipal user: User
    ) : IdViewDTO {
        val offerBody = mapper.valueToTree<ObjectNode>(offerDto);

        offerBody.put("task", id)
        offerBody.put("workerUser", user.id)
        val offer = bClient.postOffer(offerBody)

        return offer.toIdDTO();
    }

    @PostMapping("/{id}/accept")
    suspend fun acceptOfferToTask(
            @PathVariable("id") id: String,
            @RequestBody offerDto: AcceptOfferDTO,
            @AuthenticationPrincipal user: User
    ) : IdViewDTO {
        val task = bClient.getTask(id)
        val taskBody = mapper.createObjectNode();

        if (task.creatorUser != user.id) {
            throw HttpAuthorizationException("Cannot accept offers for this task")
        }

        taskBody.put("acceptedOffer", offerDto.id)
        bClient.putTask(id, taskBody)

        return task.toIdDTO();
    }

    @PostMapping("/{id}/start")
    suspend fun startTask(
            @PathVariable("id") id: String,
            @AuthenticationPrincipal user: User
    ) : IdViewDTO {
        val task = bClient.getTask(id)
        val taskBody = mapper.createObjectNode();

        if (task.workerUser != user.id) {
            throw HttpAuthorizationException("Cannot start this task")
        }
        taskBody.put("status", "in-progress")
        bClient.putTask(id, taskBody)

        return task.toIdDTO();
    }

    @PostMapping("/{id}/review")
    suspend fun reviewTask(
            @PathVariable("id") id: String,
            @RequestBody reviewDto: CreateTaskReviewDTO,
            @AuthenticationPrincipal user: User
    ): IdViewDTO {
        val task: TaskDTO = bClient.getTask(id)
        val reviewBody = mapper.valueToTree<ObjectNode>(reviewDto);
        val taskBody = mapper.createObjectNode();

        when(user.id) {
            task.creatorUser -> {
                taskBody.set("creatorRating", reviewBody)
            }
            task.workerUser -> {
                taskBody.set("workerRating", reviewBody)
            }
            else -> {
                throw HttpAuthorizationException("Cannot review this task")
            }
        }
        bClient.putTask(id, taskBody)

        return IdViewDTO(id)
    }

    // TODO: this is postponed after the release of 0.9.0
    @PostMapping("/{id}/cancel")
    suspend fun cancelTask(@RequestBody taskBody: CreateTaskDTO) : Unit {


    }
}