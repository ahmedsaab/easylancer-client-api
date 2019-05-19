package com.easylancer.api.exceptions.http

import com.fasterxml.jackson.databind.JsonNode
import org.springframework.http.HttpStatus

class HttpBadRequestException(message: String, cause: Exception? = null, data: JsonNode? = null): HttpException(HttpStatus.BAD_REQUEST, message, cause, data)