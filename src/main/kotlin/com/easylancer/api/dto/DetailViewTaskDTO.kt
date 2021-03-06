package com.easylancer.api.dto

import com.easylancer.api.data.dto.inbound.TaskLocationDTO
import com.easylancer.api.data.dto.inbound.TaskRatingDTO
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import java.util.*

@JsonPropertyOrder(alphabetic=true)
data class DetailViewTaskDTO(
        val category: String,
        val type: String,
        val paymentMethod: String,
        val description: String,
        val title: String,
        val workerUser: GeneralUserSummaryViewDTO?,
        val creatorUser: GeneralUserSummaryViewDTO,
        val price: Int,
        val seenCount: Int,
        val endDateTime: Date?,
        val creatorRating: TaskRatingDTO?,
        val workerRating: TaskRatingDTO?,
        val status: String,
        val acceptedOffer: String?,
        val imagesUrls: Array<String>,
        val tags: Array<String>,
        val id: String,
        val startDateTime: Date,
        val location: TaskLocationDTO,
        val createdAt: Date
)