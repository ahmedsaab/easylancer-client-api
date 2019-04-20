package com.easylancer.api.data.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.node.ObjectNode
import java.time.Instant
import java.util.*

@JsonIgnoreProperties(ignoreUnknown = true)
data class TaskLocationDTO(
        val lat: Float,
        val lon: Float
)