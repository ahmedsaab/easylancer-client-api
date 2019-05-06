package com.easylancer.api.controllers

import com.easylancer.api.data.*
import com.easylancer.api.data.exceptions.DataApiException
import com.easylancer.api.exceptions.TransformationException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus

abstract class BaseController() {
    protected abstract val currentUserId: String
    protected abstract val dataClient: DataAPIClient
    protected abstract val eventEmitter: EventEmitter

    protected val mapper: ObjectMapper = jacksonObjectMapper()

    protected val logger = LoggerFactory.getLogger(this.javaClass)

    @ExceptionHandler(DataApiException::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun handleDataApiMappingError(e: DataApiException): ObjectNode {
        val resp = mapper.createObjectNode()
        resp.set("error", e.toLogJson())
        resp.put("code", 500)
        resp.put("message", e.message)
        logger.error(e.message, e)
        return resp
    }

    @ExceptionHandler(TransformationException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleTransformationError(e: Exception): ObjectNode {
        val resp = mapper.createObjectNode()
        resp.put("error",e.message)
        resp.put("code",400)
        e.printStackTrace()
        return resp
    }

    @ExceptionHandler(Exception::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun handleError(e: Exception): ObjectNode {
        val resp = mapper.createObjectNode()
        resp.put("error",e.message)
        resp.put("code",500)
        e.printStackTrace()
        return resp
    }

}