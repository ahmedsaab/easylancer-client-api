package com.easylancer.api.data.dto.inbound

import com.fasterxml.jackson.databind.JsonNode

data class DataResponseErrorDTO(
        val error: String?,
        val statusCode : Int,
        val message: JsonNode
)