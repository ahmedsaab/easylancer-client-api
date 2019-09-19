package com.easylancer.api.data.dto.inbound

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.util.*

@JsonIgnoreProperties(ignoreUnknown = true)
data class TaskRatingDTO(
        val createdAt: Date,
        val rating: Int,
        val description: String,
        val like: Boolean
)