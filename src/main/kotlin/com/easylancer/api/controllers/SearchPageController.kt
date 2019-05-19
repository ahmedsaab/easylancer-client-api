package com.easylancer.api.controllers

import com.easylancer.api.data.RestClient
import com.easylancer.api.data.EventEmitter
import com.easylancer.api.data.dto.TaskDTO
import com.easylancer.api.dto.*
import kotlinx.coroutines.*

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*


@RequestMapping("/search")
@RestController
class SearchPageController(
        @Autowired val eventEmitter: EventEmitter,
        @Autowired val dataClient: RestClient
) {
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