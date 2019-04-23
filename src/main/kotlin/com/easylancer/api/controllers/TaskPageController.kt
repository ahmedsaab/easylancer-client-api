package com.easylancer.api.controllers

import com.easylancer.api.data.DataAPIClient
import com.easylancer.api.data.EventEmitter
import com.easylancer.api.data.dto.FullOfferDTO
import com.easylancer.api.data.dto.FullTaskDTO
import com.easylancer.api.data.dto.TaskDTO
import com.easylancer.api.dto.*
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import kotlinx.coroutines.*

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.web.server.ResponseStatusException


@RequestMapping("/tasks")
@RestController
@FlowPreview
class TaskPageController(
        @Autowired private val eventEmitter: EventEmitter,
        @Autowired private val dataClient: DataAPIClient
) {
    private val currentUserId = "5cb1ef55e83e494919135d9f"
    private var mapper: ObjectMapper = jacksonObjectMapper();

    @PostMapping("/create")
    suspend fun createTask(@RequestBody taskDto: CreateTaskDTO) : IdViewDTO {
        val taskBody = mapper.valueToTree<ObjectNode>(taskDto)
        taskBody.put("creatorUser", currentUserId);

        val task: TaskDTO = dataClient.postTask(taskBody)

        return task.toIdDTO();
    }

    @GetMapping("/{id}/view")
    suspend fun viewTask(@PathVariable("id") id: String) : DetailViewTaskDTO {
        val task: FullTaskDTO = dataClient.getFullTask(id)
        eventEmitter.taskSeenByUser(id, currentUserId)

        return if(task.creatorUser._id == currentUserId) {
            task.toOwnerViewTaskDTO()
        } else if (task.workerUser != null && task.workerUser._id == currentUserId){
            task.toWorkerViewTaskDTO()
        } else {
            task.toViewerViewTaskDTO()
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
    ) : IdViewDTO = coroutineScope {
        val taskBody = mapper.valueToTree<ObjectNode>(taskDto)
        val task = dataClient.getTask(id)

        if(task.creatorUser == currentUserId) {
            dataClient.putTask(id, taskBody)
        } else {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Cannot update this task")
        }

        IdViewDTO(id);
    }

    @PostMapping("/{id}/apply")
    suspend fun applyToTask(
            @PathVariable("id") id: String,
            @RequestBody offerDto: CreateOfferDTO
    ) : IdViewDTO {
        val offerBody = mapper.valueToTree<ObjectNode>(offerDto);

        offerBody.put("workerUser", currentUserId)
        val offer = dataClient.postOffer(id, offerBody)

        return offer.toIdDTO();
    }

    // TODO: do this after the payment API
    @PostMapping("/{id}/review")
    suspend fun reviewTask(@RequestBody taskBody: CreateTaskDTO): Unit {

    }

    // TODO: do this after the payment API
    @PostMapping("/{id}/cancel")
    suspend fun cancelTask(@RequestBody taskBody: CreateTaskDTO) : Unit {

    }

    /** Handle the error */
    @ExceptionHandler(Exception::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun handleError(e: Exception): ObjectNode {
        val resp = JsonNodeFactory.instance.objectNode()
        resp.put("error",e.message)
        resp.put("code",500)
        e.printStackTrace()
        return resp
    }
}