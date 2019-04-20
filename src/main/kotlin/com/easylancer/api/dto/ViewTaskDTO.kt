package com.easylancer.api.dto

import com.easylancer.api.data.dto.TaskLocationDTO
import com.easylancer.api.data.dto.TaskRatingDTO
import com.easylancer.api.data.dto.UserSummaryDTO
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import java.util.*

@JsonPropertyOrder(alphabetic=true)
data class ViewTaskDTO(
    val category: String,
    val type: String,
    val paymentMethod: String,
    val description: String,
    val title: String,
    val workerUser: UserSummaryDTO?,
    val creatorUser: UserSummaryDTO,
    val price: Int,
    val seenCount: Int,
    val endDateTime: Date?,
    val creatorRating: TaskRatingDTO?,
    val workerRating: TaskRatingDTO?,
    val status: String,
    val acceptedOffer: String?,
    val imagesUrls: Array<String>,
    val id: String,
    val startDateTime: Date,
    val location: TaskLocationDTO,
    val createdAt: Date
)