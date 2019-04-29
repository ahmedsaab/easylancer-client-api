package com.easylancer.api.controllers

import com.easylancer.api.data.DataAPIClient
import com.easylancer.api.data.EventEmitter
import com.easylancer.api.data.dto.TaskDTO
import com.easylancer.api.dto.*
import kotlinx.coroutines.*

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*


@RequestMapping("/search")
@RestController
@FlowPreview
class SearchPageController(
        @Autowired override val eventEmitter: EventEmitter,
        @Autowired override val dataClient: DataAPIClient,
        @Autowired override val currentUserId: String
) : BaseController() {
    @GetMapping("/all")
    suspend fun viewAllTasks() : List<ListViewTaskDTO> {
        val tasks: Array<TaskDTO> = dataClient.getAllTasks();

        return tasks.map { it.toListViewTaskDTO() }
    }

    // TODO: implement filter on API
    @GetMapping("/open")
    suspend fun viewOpenTasks() : List<ListViewTaskDTO> {
        val tasks: Array<TaskDTO> = dataClient.getAllTasks();

        return tasks.map { it.toListViewTaskDTO() }
    }
}