package com.easylancer.api.data.dto.inbound

import com.easylancer.api.data.dto.types.TaskStatus
import com.easylancer.api.dto.AppliedViewTaskDTO
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.bson.types.ObjectId
import java.util.*

@JsonIgnoreProperties(ignoreUnknown = true)
data class AppliedTaskDTO(
        val category: String,
        val type: String,
        val paymentMethod: String,
        val description: String,
        val title: String,
        val workerUser: ObjectId?,
        val creatorUser: GeneralUserSummaryDTO,
        val price: Int,
        val seenBy: Array<String>,
        val endDateTime: Date?,
        val status: TaskStatus,
        val acceptedOffer: String?,
        val imagesUrls: Array<String>,
        val creatorRating: TaskRatingDTO?,
        val workerRating: TaskRatingDTO?,
        val _id: ObjectId,
        val startDateTime: Date,
        val location: TaskLocationDTO,
        val createdAt: Date,
        val tags: Array<String>,
        val offer: OfferDTO
) {
    fun toView() = AppliedViewTaskDTO(
        id = _id.toHexString(),
        category = category,
        createdAt = createdAt,
        location = location,
        offer = offer.toOfferSummaryViewDTO(),
        paymentMethod = paymentMethod,
        price = price,
        startDateTime = startDateTime,
        status = status,
        title = title,
        type = type,
        creatorUser = creatorUser.toGeneralUserSummaryViewDTO()
    )
}