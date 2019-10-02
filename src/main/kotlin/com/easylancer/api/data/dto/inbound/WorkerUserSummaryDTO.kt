package com.easylancer.api.data.dto.inbound

import com.easylancer.api.dto.WorkerUserSummaryViewDTO
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.bson.types.ObjectId

@JsonIgnoreProperties(ignoreUnknown = true)
data class WorkerUserSummaryDTO(
        val firstName: String?,
        val lastName: String?,
        val imageUrl: String?,
        val isApproved: Boolean,
        val _id: ObjectId,
        val badges: Array<UserBadgeDTO>,
        val tags: Array<UserTagDTO>,
        val ratings: UserRatingsDTO
) {
    fun toWorkerUserSummaryViewDTO() = WorkerUserSummaryViewDTO(
            id = _id.toHexString(),
            firstName = firstName,
            lastName = lastName,
            imageUrl = imageUrl,
            isApproved = isApproved,
            badges = badges,
            tags = tags,
            ratings = ratings
    )
}