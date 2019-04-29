package com.easylancer.api.data.dto

import com.fasterxml.jackson.databind.JsonNode

data class ErrorDTO(
        val statusCode : Int,
        val message: JsonNode
)