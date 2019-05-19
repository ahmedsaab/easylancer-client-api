package com.easylancer.api.exceptions.http

import com.fasterxml.jackson.databind.JsonNode
import org.springframework.core.NestedRuntimeException
import org.springframework.http.HttpStatus

abstract class HttpException(
        val status: HttpStatus,
        override val message: String,
        cause: Exception? = null,
        val data: JsonNode? = null
) : NestedRuntimeException(message, cause)