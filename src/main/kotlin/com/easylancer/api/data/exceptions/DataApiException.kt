package com.easylancer.api.data.exceptions

import com.easylancer.api.data.http.DataRequest
import com.easylancer.api.data.http.DataResponse
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

abstract class DataApiException(
        override val message: String,
        open val dataRequest: DataRequest,
        open val response: DataResponse? = null,
        private val error: Exception? = null
) : RuntimeException(message, error) {
    fun toLogJson(): JsonNode {
        val log = jacksonObjectMapper().createObjectNode()
        log.set("dataRequest", jacksonObjectMapper().valueToTree(dataRequest))
        log.set("dataResponseError", jacksonObjectMapper().valueToTree(response))
        log.put("message", error?.message)
        return log
    }
}