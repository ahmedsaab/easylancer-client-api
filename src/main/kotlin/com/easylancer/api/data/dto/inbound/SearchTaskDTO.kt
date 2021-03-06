package com.easylancer.api.data.dto.inbound

import com.easylancer.api.data.dto.types.TaskStatus
import com.easylancer.api.dto.SearchViewTaskDTO
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.bson.types.ObjectId
import java.util.*

@JsonIgnoreProperties(ignoreUnknown = true)
data class SearchTaskDTO(
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
        val tags: Array<String>
) {
    fun toSearchViewTaskDTO() = SearchViewTaskDTO(
        category = category,
        type = type,
        paymentMethod = paymentMethod,
        title = title,
        price = price,
        endDateTime = endDateTime,
        status = status.displayName,
        id = _id.toHexString(),
        startDateTime = startDateTime,
        location = location,
        createdAt = createdAt,
        creatorUser = creatorUser.toGeneralUserSummaryViewDTO(),
        imagesUrls = imagesUrls,
        tags = tags
    )
}