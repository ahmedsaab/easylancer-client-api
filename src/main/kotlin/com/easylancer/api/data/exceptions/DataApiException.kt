package com.easylancer.api.data.exceptions

import com.easylancer.api.data.DataApiRequest
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

abstract class DataApiException(message: String, request: DataApiRequest): RuntimeException(message) {
    protected val mapper: ObjectMapper = jacksonObjectMapper()
    abstract fun toLogJson(): JsonNode
}