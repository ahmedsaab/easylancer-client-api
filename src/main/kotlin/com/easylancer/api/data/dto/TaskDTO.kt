package com.easylancer.api.data.dto

import com.easylancer.api.dto.IdViewDTO
import com.easylancer.api.dto.ListViewTaskDTO
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.util.*

@JsonIgnoreProperties(ignoreUnknown = true)
data class TaskDTO(
    val category: String,
    val type: String,
    val paymentMethod: String,
    val description: String,
    val title: String,
    val workerUser: String?,
    val creatorUser: String?,
    val price: Int,
    val seenBy: Array<String>,
    val endDateTime: Date?,
    val status: String,
    val acceptedOffer: String?,
    val imagesUrls: Array<String>,
    val creatorRating: TaskRatingDTO?,
    val workerRating: TaskRatingDTO?,
    val _id: String,
    val startDateTime: Date,
    val location: TaskLocationDTO,
    val createdAt: Date
) {
    fun toIdDTO() = IdViewDTO(
        id = _id
    )
    fun toListViewTaskDTO() = ListViewTaskDTO(
        category = category,
        type = type,
        paymentMethod = paymentMethod,
        title = title,
        price = price,
        endDateTime = endDateTime,
        status = status,
        id = _id,
        startDateTime = startDateTime,
        location = location,
        createdAt = createdAt
    )
}