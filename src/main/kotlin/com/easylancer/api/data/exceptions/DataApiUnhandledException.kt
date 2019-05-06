package com.easylancer.api.data.exceptions

import com.easylancer.api.data.DataRequest
import com.fasterxml.jackson.databind.JsonNode

class DataApiUnhandledException(message: String, request: DataRequest, e: Exception): DataApiException(message + ": ${e.message}", request, e) {
    override val reason: JsonNode? = null
}
