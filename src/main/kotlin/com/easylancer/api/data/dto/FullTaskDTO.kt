package com.easylancer.api.data.dto

import com.easylancer.api.dto.ViewTaskDTO
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.util.*

/**
 * Representation of a Task
 * @property username The username of the user
 * @property screenName The screen name of the user
 * @property email The email address of the user
 * @property registered When the user registered with us
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class FullTaskDTO(
    val category: String,
    val type: String,
    val paymentMethod: String,
    val description: String,
    val title: String,
    val workerUser: UserSummaryDTO?,
    val creatorUser: UserSummaryDTO,
    val price: Int,
    val seenBy: Array<String>,
    val endDateTime: Date?,
    val creatorRating: TaskRatingDTO?,
    val workerRating: TaskRatingDTO?,
    val status: String,
    val acceptedOffer: String?,
    val imagesUrls: Array<String>,
    val _id: String,
    val startDateTime: Date,
    val location: TaskLocationDTO,
    val createdAt: Date
) {
    fun toOwnerViewTaskDTO() = ViewTaskDTO(
        category = category,
        type = type,
        paymentMethod = paymentMethod,
        description = description,
        title = title,
        workerUser = workerUser,
        creatorUser = creatorUser,
        price = price,
        seenCount = seenBy.size,
        endDateTime = endDateTime,
        creatorRating = creatorRating,
        workerRating = workerRating,
        status = status,
        acceptedOffer = acceptedOffer,
        imagesUrls = imagesUrls,
        id = _id,
        startDateTime = startDateTime,
        location = location,
        createdAt = createdAt
    )

    fun toWorkerViewTaskDTO() = ViewTaskDTO(
        category = category,
        type = type,
        paymentMethod = paymentMethod,
        description = description,
        title = title,
        workerUser = workerUser,
        creatorUser = creatorUser,
        price = price,
        seenCount = seenBy.size,
        endDateTime = endDateTime,
        creatorRating = creatorRating,
        workerRating = workerRating,
        status = status,
        acceptedOffer = acceptedOffer,
        imagesUrls = imagesUrls,
        id = _id,
        startDateTime = startDateTime,
        location = location,
        createdAt = createdAt
    )

    fun toViewerViewTaskDTO() = ViewTaskDTO(
        category = category,
        type = type,
        paymentMethod = paymentMethod,
        description = description,
        title = title,
        workerUser = null,
        creatorUser = creatorUser,
        price = price,
        seenCount = seenBy.size,
        endDateTime = endDateTime,
        creatorRating = creatorRating,
        workerRating = workerRating,
        status = status,
        acceptedOffer = acceptedOffer,
        imagesUrls = imagesUrls,
        id = _id,
        startDateTime = startDateTime,
        location = location,
        createdAt = createdAt
    )
}