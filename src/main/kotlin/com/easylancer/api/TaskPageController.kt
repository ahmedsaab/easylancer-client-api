package com.easylancer.api

import com.fasterxml.jackson.databind.node.ObjectNode
import kotlinx.coroutines.*

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import org.springframework.web.reactive.function.client.awaitBody
import org.springframework.web.reactive.function.client.awaitExchange
import org.springframework.web.server.ResponseStatusException
import java.time.Instant
import javax.annotation.PreDestroy
import javax.xml.crypto.Data
import kotlin.reflect.jvm.internal.impl.load.kotlin.JvmType
import com.fasterxml.jackson.databind.node.JsonNodeFactory



@RequestMapping("/tasks")
@RestController
@FlowPreview
class TaskPageController(
        @Autowired private val webClient: WebClient,
        @Autowired private val restTemplate: RestTemplate,
        @Autowired private val dataClient: DataAPIClient
) {
    private val currentUser = "5cb1ef55e83e494919135d9f"

    @GetMapping("/{id}/view")
    suspend fun getTask(@PathVariable("id") id: String) : TaskDTO = coroutineScope {
        val taskAsync = async {
            dataClient.getTask(id)
        }
        GlobalScope.launch {
            try {
                dataClient.taskSeenBy(id, currentUser)
            } catch(e: DataApiException) {
                println("Task seen by Request failed: ${e.message}")
            }
        };
        taskAsync.await();
    }

    /** Handle the error */
    @ExceptionHandler(Exception::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun handleError(e: Exception): ObjectNode {
        val resp = JsonNodeFactory.instance.objectNode()
        resp.put("error",e.message)
        resp.put("code",500)
        return resp
    }
}