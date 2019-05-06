package com.easylancer.api.data.exceptions

import com.easylancer.api.data.DataRequest
import com.easylancer.api.data.dto.DataResponseErrorDTO
import com.fasterxml.jackson.databind.JsonNode

class DataApiResponseException(message: String, dataErrorResponse: DataResponseErrorDTO, statusCode: Int, request: DataRequest): DataApiException(message, request) {
    override val reason: JsonNode = mapper.valueToTree(ResponseExceptionReason(dataErrorResponse, statusCode))
}

data class ResponseExceptionReason(val body: DataResponseErrorDTO, val statusCode: Int)
