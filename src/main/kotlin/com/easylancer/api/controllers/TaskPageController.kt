package com.easylancer.api.controllers

import com.easylancer.api.data.DataAPIClient
import com.easylancer.api.data.EventEmitter
import com.easylancer.api.data.dto.FullTaskDTO
import com.easylancer.api.dto.CreateTaskDTO
import com.easylancer.api.dto.IdDTO
import com.easylancer.api.dto.ViewTaskDTO
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import kotlinx.coroutines.*

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import java.text.SimpleDateFormat
import java.text.DateFormat
import java.util.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter




@RequestMapping("/tasks")
@RestController
@FlowPreview
class TaskPageController(
        @Autowired private val eventEmitter: EventEmitter,
        @Autowired private val dataClient: DataAPIClient
) {
    private val currentUserId = "5cb1ef55e83e494919135d9f"

    @GetMapping("/{id}/view")
    suspend fun viewTask(@PathVariable("id") id: String) : ViewTaskDTO {
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

    @PostMapping("/create")
    suspend fun createTask(@RequestBody taskBody: CreateTaskDTO) : IdDTO {
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