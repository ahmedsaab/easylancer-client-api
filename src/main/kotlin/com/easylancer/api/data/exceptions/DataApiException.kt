package com.easylancer.api.data.exceptions

import com.easylancer.api.data.http.DataRequest
import com.easylancer.api.data.http.DataResponse
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.core.NestedRuntimeException

abstract class DataApiException(
        override val message: String,
        open val request: DataRequest,
        open val response: DataResponse? = null,
        cause: Throwable? = null
) : NestedRuntimeException(message, cause) {
    fun toLogJson(): JsonNode {
        val log = jacksonObjectMapper().createObjectNode()
        log.set("request", jacksonObjectMapper().valueToTree(request))
        log.set("response", jacksonObjectMapper().valueToTree(response))
        log.put("message", message)
        log.put("cause", cause?.message)
        return log
    }
}