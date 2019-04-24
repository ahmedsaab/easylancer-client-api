package com.easylancer.api.controllers

import com.easylancer.api.data.DataAPIClient
import com.easylancer.api.data.dto.TaskDTO
import com.easylancer.api.dto.*
import com.fasterxml.jackson.databind.node.ObjectNode
import kotlinx.coroutines.*

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import com.fasterxml.jackson.databind.node.JsonNodeFactory


@RequestMapping("/search")
@RestController
@FlowPreview
class SearchPageController(
        @Autowired private val dataClient: DataAPIClient
) {
    @GetMapping("/all")
    suspend fun viewAllTasks() : List<ListViewTaskDTO> {
        val tasks: Array<TaskDTO> = dataClient.getAllTasks();

        return tasks.map { it.toListViewTaskDTO() }
    }

    // TODO: implement
    @GetMapping("/open")
    suspend fun viewOpenTasks() : List<ListViewTaskDTO> {
        val tasks: Array<TaskDTO> = dataClient.getAllTasks();

        return tasks.map { it.toListViewTaskDTO() }
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