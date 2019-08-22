package com.easylancer.api.data.dto

import com.easylancer.api.dto.DetailViewTaskDTO
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.bson.types.ObjectId
import java.util.*

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
        val acceptedOffer: ObjectId?,
        val imagesUrls: Array<String>,
        val _id: ObjectId,
        val startDateTime: Date,
        val location: TaskLocationDTO,
        val createdAt: Date,
        val tags: Array<String>
) {
    fun toOwnerViewTaskDTO() = DetailViewTaskDTO(
        category = category,
        type = type,
        paymentMethod = paymentMethod,
        description = description,
        title = title,
        workerUser = workerUser?.toUserSummaryViewDTO(),
        creatorUser = creatorUser.toUserSummaryViewDTO(),
        price = price,
        seenCount = seenBy.size,
        endDateTime = endDateTime,
        creatorRating = creatorRating,
        workerRating = workerRating,
        status = status,
        acceptedOffer = acceptedOffer?.toHexString(),
        imagesUrls = imagesUrls,
        id = _id.toHexString(),
        startDateTime = startDateTime,
        location = location,
        createdAt = createdAt,
        tags = tags
    )

    fun toWorkerViewTaskDTO() = DetailViewTaskDTO(
        category = category,
        type = type,
        paymentMethod = paymentMethod,
        description = description,
        title = title,
        workerUser = workerUser?.toUserSummaryViewDTO(),
        creatorUser = creatorUser.toUserSummaryViewDTO(),
        price = price,
        seenCount = seenBy.size,
        endDateTime = endDateTime,
        creatorRating = creatorRating,
        workerRating = workerRating,
        status = status,
        acceptedOffer = acceptedOffer?.toHexString(),
        imagesUrls = imagesUrls,
        id = _id.toHexString(),
        startDateTime = startDateTime,
        location = location,
        createdAt = createdAt,
        tags = tags
    )

    fun toViewerViewTaskDTO() = DetailViewTaskDTO(
        category = category,
        type = type,
        paymentMethod = paymentMethod,
        description = description,
        title = title,
        workerUser = null,
        creatorUser = creatorUser.toUserSummaryViewDTO(),
        price = price,
        seenCount = seenBy.size,
        endDateTime = endDateTime,
        creatorRating = creatorRating,
        workerRating = workerRating,
        status = status,
        acceptedOffer = null,
        imagesUrls = imagesUrls,
        id = _id.toHexString(),
        startDateTime = startDateTime,
        location = location,
        createdAt = createdAt,
        tags = tags
    )
}