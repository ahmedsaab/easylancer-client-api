package com.easylancer.api.data.exceptions

import com.easylancer.api.data.DataApiRequest
import com.fasterxml.jackson.databind.JsonNode

class DataApiUnknownResponseException(message: String, request: DataApiRequest): DataApiException(message, request) {
    override fun toLogJson(): JsonNode {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}