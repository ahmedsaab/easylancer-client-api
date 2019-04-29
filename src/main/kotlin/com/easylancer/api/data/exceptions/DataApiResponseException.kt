package com.easylancer.api.data.exceptions

import com.easylancer.api.data.DataApiRequest
import com.easylancer.api.data.dto.ErrorDTO
import com.fasterxml.jackson.databind.JsonNode

class DataApiResponseException(error: ErrorDTO, statusCode: Int, request: DataApiRequest): DataApiException(error.message.toString(), request) {
    override fun toLogJson(): JsonNode {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}