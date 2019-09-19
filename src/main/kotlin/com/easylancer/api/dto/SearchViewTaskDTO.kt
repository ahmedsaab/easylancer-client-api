package com.easylancer.api.dto

import com.easylancer.api.data.dto.inbound.TaskLocationDTO
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import java.util.*

@JsonPropertyOrder(alphabetic=true)
data class SearchViewTaskDTO(
        val category: String,
        val type: String,
        val paymentMethod: String,
        val title: String,
        val price: Int,
        val endDateTime: Date?,
        val status: String,
        val id: String,
        val startDateTime: Date,
        val location: TaskLocationDTO,
        val createdAt: Date,
        val imagesUrls: Array<String>,
        val creatorUser: GeneralUserSummaryViewDTO
)