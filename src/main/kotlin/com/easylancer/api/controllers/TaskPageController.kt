package com.easylancer.api.controllers

import com.easylancer.api.data.DataAPIClient
import com.easylancer.api.data.EventEmitter
import com.easylancer.api.data.dto.FullTaskDTO
import com.easylancer.api.data.dto.TaskDTO
import com.easylancer.api.data.exceptions.DataApiResponseException
import com.easylancer.api.dto.*
import com.easylancer.api.exceptions.HandledNotFoundException
import com.fasterxml.jackson.databind.node.ObjectNode
import kotlinx.coroutines.*

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@FlowPreview
@RequestMapping("/tasks")
class TaskPageController(
        @Autowired override val eventEmitter: EventEmitter,
        @Autowired override val dataClient: DataAPIClient,
        @Autowired override val currentUserId: String
) : BaseController() {

    @PostMapping("/create")
    suspend fun createTask(@RequestBody taskDto: CreateTaskDTO) : IdViewDTO {
        val taskBody = mapper.valueToTree<ObjectNode>(taskDto)
        taskBody.put("creatorUser", currentUserId);

        val task: TaskDTO = dataClient.postTask(taskBody)

        return task.toIdDTO();
    }

    @GetMapping("/{id}/view")
    suspend fun viewTask(@PathVariable("id") id: String) : DetailViewTaskDTO {
        try {
            val task: FullTaskDTO = dataClient.getFullTask(id)
            eventEmitter.taskSeenByUser(id, currentUserId)

            return if(task.creatorUser._id == currentUserId) {
                task.toOwnerViewTaskDTO()
            } else if (task.workerUser != null && task.workerUser._id == currentUserId){
                task.toWorkerViewTaskDTO()
            } else {
                task.toViewerViewTaskDTO()
            }
        } catch (e: DataApiResponseException) {
            if(e.response.statusCode == HttpStatus.NOT_FOUND.value()) {
                throw HandledNotFoundException("Task not found")
            }
            throw e
        }

    }

    @GetMapping("/{id}/offers")
    suspend fun viewTaskOffers(@PathVariable("id") id: String) : List<ViewOfferDTO> = coroutineScope {
        val offersAsync = async { dataClient.getTaskOffers(id) }
        val taskAsync = async { dataClient.getTask(id) }

        offersAsync.await().filter {
            it.workerUser._id == currentUserId || taskAsync.await().creatorUser == currentUserId
        }.map {
            it.toViewOfferDTO()
        }
    }

    @PutMapping("/{id}/edit")
    suspend fun updateTask(
            @PathVariable("id") id: String,
            @RequestBody taskDto: UpdateTaskDTO
    ) : IdViewDTO {
        val taskBody = mapper.valueToTree<ObjectNode>(taskDto)
        val task = dataClient.getTask(id)

        if(task.creatorUser == currentUserId) {
            dataClient.putTask(id, taskBody)
        } else {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Cannot update this task")
        }

        return IdViewDTO(id);
    }

    @PostMapping("/{id}/apply")
    suspend fun applyToTask(
            @PathVariable("id") id: String,
            @RequestBody offerDto: CreateOfferDTO
    ) : IdViewDTO {
        val offerBody = mapper.valueToTree<ObjectNode>(offerDto);

        offerBody.put("task", id)
        offerBody.put("workerUser", currentUserId)
        val offer = dataClient.postOffer(offerBody)

        return offer.toIdDTO();
    }

    @PostMapping("/{id}/accept")
    suspend fun acceptOfferToTask(
            @PathVariable("id") id: String,
            @RequestBody offerDto: AcceptOfferDTO
    ) : IdViewDTO {
        val task = dataClient.getTask(id)
        val taskBody = mapper.createObjectNode();

        if (task.creatorUser != currentUserId) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Cannot accept offers for this task")
        }

        taskBody.put("acceptedOffer", offerDto.id)
        dataClient.putTask(id, taskBody)

        return task.toIdDTO();
    }

    @PostMapping("/{id}/start")
    suspend fun startTask(
            @PathVariable("id") id: String
    ) : IdViewDTO {
        val task = dataClient.getTask(id)
        val taskBody = mapper.createObjectNode();

        if (task.workerUser != currentUserId) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Cannot start this task")
        }
        taskBody.put("status", "in-progress")
        dataClient.putTask(id, taskBody)

        return task.toIdDTO();
    }

    @PostMapping("/{id}/review")
    suspend fun reviewTask(
            @PathVariable("id") id: String,
            @RequestBody reviewDto: CreateTaskReviewDTO
    ): IdViewDTO {
        val task: TaskDTO = dataClient.getTask(id)
        val reviewBody = mapper.valueToTree<ObjectNode>(reviewDto);
        val taskBody = mapper.createObjectNode();

        when(currentUserId) {
            task.creatorUser -> {
                taskBody.set("creatorRating", reviewBody)
            }
            task.workerUser -> {
                taskBody.set("workerRating", reviewBody)
            }
            else -> {
                throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Cannot review this task")
            }
        }
        dataClient.putTask(id, taskBody)

        return IdViewDTO(id)
    }

    // TODO: this is postponed after the release of 0.9.0
    @PostMapping("/{id}/cancel")
    suspend fun cancelTask(@RequestBody taskBody: CreateTaskDTO) : Unit {


    }
}