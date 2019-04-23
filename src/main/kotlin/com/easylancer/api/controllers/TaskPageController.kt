package com.easylancer.api.controllers

import com.easylancer.api.data.DataAPIClient
import com.easylancer.api.data.EventEmitter
import com.easylancer.api.data.dto.FullOfferDTO
import com.easylancer.api.data.dto.FullTaskDTO
import com.easylancer.api.data.dto.TaskDTO
import com.easylancer.api.dto.*
import com.fasterxml.jackson.databind.node.ObjectNode
import kotlinx.coroutines.*

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import com.fasterxml.jackson.databind.node.JsonNodeFactory


@RequestMapping("/tasks")
@RestController
@FlowPreview
class TaskPageController(
        @Autowired private val eventEmitter: EventEmitter,
        @Autowired private val dataClient: DataAPIClient
) {
    private val currentUserId = "5cbf379768204b27444108a9"

    @GetMapping("/view")
    suspend fun viewAllTasks() : List<ListViewTaskDTO> {
        val tasks: Array<TaskDTO> = dataClient.getAllTasksAsync().await();

        return tasks.map { it.toListViewTaskDTO() }
    }

    @PostMapping("/create")
    suspend fun createTask(@RequestBody taskBody: CreateTaskDTO) : IdViewDTO {
        val task: TaskDTO = dataClient.postTaskAsync(currentUserId, taskBody).await()

        return task.toIdDTO();
    }

    @GetMapping("/{id}/view")
    suspend fun viewTask(@PathVariable("id") id: String) : DetailViewTaskDTO {
        val task: FullTaskDTO = dataClient.getTaskAsync(id).await()
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
    suspend fun viewTaskOffers(@PathVariable("id") id: String) : List<ViewOfferDTO> {
        val offers: Array<FullOfferDTO> = dataClient.getTaskOffersAsync(id).await()

        return offers.map { it.toViewOfferDTO() }
    }

    @PutMapping("/{id}/edit")
    suspend fun updateTask(
            @PathVariable("id") id: String,
            @RequestBody taskBody: UpdateTaskDTO
    ) : IdViewDTO {
        dataClient.putTaskAsync(id, currentUserId, taskBody).await()

        return IdViewDTO(id);
    }

    @PostMapping("/{id}/apply")
    suspend fun applyToTask(
            @PathVariable("id") id: String,
            @RequestBody offerBody: CreateOfferDTO
    ) : IdViewDTO {
        val offer = dataClient.postOfferAsync(currentUserId, id, offerBody).await()

        return offer.toIdDTO();
    }

    @PostMapping("/{id}/review")
    suspend fun reviewTask(@RequestBody taskBody: CreateTaskDTO): Unit {

    }

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