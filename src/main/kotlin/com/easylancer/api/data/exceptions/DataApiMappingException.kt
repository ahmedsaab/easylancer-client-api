package com.easylancer.api.data.exceptions

import com.easylancer.api.data.DataRequest
import com.fasterxml.jackson.databind.JsonNode

class DataApiMappingException(message: String, mappingParams: MappingExceptionReason, request: DataRequest): DataApiException(message, request) {
    override val reason: JsonNode = mapper.valueToTree(mappingParams)
}

data class MappingExceptionReason(val fromInstance: JsonNode, val toClass: String)