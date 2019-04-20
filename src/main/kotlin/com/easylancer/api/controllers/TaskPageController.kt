package com.easylancer.api.controllers

import com.easylancer.api.data.DataAPIClient
import com.easylancer.api.data.EventEmitter
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
    private val currentUserId = "5cb21c90d70d4f0548b09775"

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

    @GetMapping("/view")
    suspend fun viewAllTasks() : List<ListViewTaskDTO> {
        val tasks: Array<TaskDTO> = dataClient.getAllTasksAsync().await();

        return tasks.map { it.toListViewTaskDTO() }
    }

    @PostMapping("/create")
    suspend fun createTask(@RequestBody taskBody: CreateTaskDTO) : IdDTO {
        val task: TaskDTO = dataClient.postTaskAsync(currentUserId, taskBody).await()

        return task.toIdDTO();
    }

    @PutMapping("/{id}/edit")
    suspend fun updateTask(
            @PathVariable("id") id: String,
            @RequestBody taskBody: UpdateTaskDTO
    ) : IdDTO {
        dataClient.putTaskAsync(id, currentUserId, taskBody).await()

        return IdDTO(id);
    }

    @PostMapping("/{id}/apply")
    suspend fun applyToTask(
            @PathVariable("id") id: String,
            @RequestBody offerBody: CreateOfferDTO
    ) : IdDTO {
        val offer = dataClient.postOfferAsync(currentUserId, id, offerBody).await()

        return offer.toIdDTO();
    }

    @PostMapping("/{id}/review")
    suspend fun reviewTask(@RequestBody taskBody: CreateTaskDTO) : IdDTO {
        val task = dataClient.postTaskAsync(currentUserId, taskBody).await()

        return task.toIdDTO();
    }

    @PostMapping("/{id}/cancel")
    suspend fun cancelTask(@RequestBody taskBody: CreateTaskDTO) : IdDTO {
        val task = dataClient.postTaskAsync(currentUserId, taskBody).await()

        return task.toIdDTO();
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