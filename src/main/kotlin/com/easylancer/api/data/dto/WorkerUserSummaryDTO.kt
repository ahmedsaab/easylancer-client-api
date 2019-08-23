package com.easylancer.api.data.dto

import com.easylancer.api.dto.GeneralUserSummaryViewDTO
import com.easylancer.api.dto.WorkerUserSummaryViewDTO
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.bson.types.ObjectId

@JsonIgnoreProperties(ignoreUnknown = true)
data class WorkerUserSummaryDTO(
        val firstName: String?,
        val lastName: String?,
        val imageUrl: String?,
        val dislikes: Int,
        val likes: Int,
        val isApproved: Boolean,
        val _id: ObjectId,
        val badges: Array<UserBadgeDTO>,
        val tags: Array<UserTagDTO>
) {
    fun toWorkerUserSummaryViewDTO() = WorkerUserSummaryViewDTO(
            id = _id.toHexString(),
            firstName = firstName,
            lastName = lastName,
            imageUrl = imageUrl,
            likes = likes,
            dislikes = dislikes,
            isApproved = isApproved,
            badges = badges,
            tags = tags
    )
}