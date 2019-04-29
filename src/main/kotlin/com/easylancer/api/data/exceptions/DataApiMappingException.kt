package com.easylancer.api.data.exceptions

import com.easylancer.api.data.DataApiRequest
import com.fasterxml.jackson.databind.JsonNode

class DataApiMappingException(private val mappingParams: MappingParams, private val request: DataApiRequest): DataApiException(mappingParams.toMessage(), request) {
    override fun toLogJson(): JsonNode {
        val log = mapper.createObjectNode();
        log.put("message", message)
        log.set("reason", mapper.valueToTree(mappingParams))
        log.set("request", mapper.valueToTree(request))
        return log
    }
}

data class MappingParams(val fromInstance: JsonNode, val toClass: String) {
    fun toMessage(): String {
        return "Failed to map $fromInstance to class $toClass"
    }
}