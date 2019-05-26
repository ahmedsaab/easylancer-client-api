package com.easylancer.api.dto

import com.fasterxml.jackson.databind.JsonNode

/**
 * Error DTO, to be sent as response body
 * in case of errors
 */

data class ErrorResponseDTO (
        val data: JsonNode? = null,
        val message: String,
        val status: Int
)