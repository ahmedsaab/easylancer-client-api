package com.easylancer.api.data.dto.inbound

import com.easylancer.api.data.dto.types.TaskStatus
import com.easylancer.api.dto.IdViewDTO
import com.easylancer.api.dto.ListViewTaskDTO
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.bson.types.ObjectId
import java.util.*

@JsonIgnoreProperties(ignoreUnknown = true)
data class TaskDTO(
        val category: String,
        val type: String,
        val paymentMethod: String,
        val description: String,
        val title: String,
        val workerUser: ObjectId?,
        val creatorUser: ObjectId,
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
    fun toIdDTO() = IdViewDTO(
        id = _id.toHexString()
    )

    fun toListViewTaskDTO() = ListViewTaskDTO(
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
        createdAt = createdAt
    )

    fun isReviewedBy(userId: ObjectId): Boolean {
        return when (userId) {
            this.workerUser -> this.workerRating != null
            this.creatorUser -> this.creatorRating != null
            else -> false
        }
    }
}