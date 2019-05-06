package com.easylancer.api.data.dto

import com.fasterxml.jackson.databind.JsonNode

data class DataResponseErrorDTO(
        val error: String?,
        val statusCode : Int,
        val message: JsonNode
)