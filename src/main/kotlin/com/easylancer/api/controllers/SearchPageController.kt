package com.easylancer.api.controllers

import com.easylancer.api.data.EventEmitter
import com.easylancer.api.data.dto.TaskDTO
import com.easylancer.api.dto.*

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*


@RequestMapping("/search")
@RestController
class SearchPageController(
        @Autowired val eventEmitter: EventEmitter,
        @Autowired private val bClient: com.easylancer.api.data.blocking.DataApiClient
) {
    @GetMapping("/all")
    suspend fun viewAllTasks() : List<ListViewTaskDTO> {
        val tasks: Array<TaskDTO> = bClient.getAllTasks();

        return tasks.map { it.toListViewTaskDTO() }
    }

    // TODO: implement filter on API
    @GetMapping("/open")
    suspend fun viewOpenTasks() : List<ListViewTaskDTO> {
        val tasks: Array<TaskDTO> = bClient.getAllTasks();

        return tasks.map { it.toListViewTaskDTO() }
    }
}