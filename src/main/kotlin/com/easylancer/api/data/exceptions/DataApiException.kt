package com.easylancer.api.data.exceptions

import com.easylancer.api.data.Request
import com.easylancer.api.data.DataResponse
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

abstract class DataApiException(
        override val message: String,
        private val request: Request,
        open val response: DataResponse? = null,
        private val error: Exception? = null
) : RuntimeException(message, error) {
    fun toLogJson(): JsonNode {
        val log = jacksonObjectMapper().createObjectNode()
        log.set("request", jacksonObjectMapper().valueToTree(request))
        log.set("response", jacksonObjectMapper().valueToTree(response))
        log.put("message", error?.message)
        return log
    }
}