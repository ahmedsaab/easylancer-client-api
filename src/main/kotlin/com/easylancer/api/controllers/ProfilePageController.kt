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


@RequestMapping("/profile")
@RestController
@FlowPreview
class ProfilePageController(
        @Autowired private val eventEmitter: EventEmitter,
        @Autowired private val dataClient: DataAPIClient
) {
    private val currentUserId = "5cbf379768204b27444108a9"

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