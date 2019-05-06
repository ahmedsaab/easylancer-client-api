package com.easylancer.api.data.exceptions

import com.easylancer.api.data.DataRequest
import com.fasterxml.jackson.databind.JsonNode

class DataApiUnknownResponseException(message: String, request: DataRequest): DataApiException(message, request) {
    override val reason: JsonNode? = null
}
