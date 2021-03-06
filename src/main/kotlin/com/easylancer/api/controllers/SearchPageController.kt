package com.easylancer.api.controllers

import com.easylancer.api.data.DataApiClient
import com.easylancer.api.dto.*

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/search")
class SearchPageController(
        @Autowired private val client: DataApiClient
) {
    @GetMapping("/all")
    fun viewAllTasks() : Mono<List<SearchViewTaskDTO>> {
        return client.getAllTasks().map {
            it.toSearchViewTaskDTO()
        }.collectList()
    }

    // TODO: implement filter on API
    @GetMapping("/open")
    fun viewOpenTasks() : Mono<List<SearchViewTaskDTO>> {
        return client.getAllTasks().map {
            it.toSearchViewTaskDTO()
        }.collectList()
    }
}