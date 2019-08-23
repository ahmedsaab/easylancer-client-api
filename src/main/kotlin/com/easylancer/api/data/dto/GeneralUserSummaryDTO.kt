package com.easylancer.api.data.dto

import com.easylancer.api.dto.GeneralUserSummaryViewDTO
import com.easylancer.api.dto.WorkerUserSummaryViewDTO
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.bson.types.ObjectId

@JsonIgnoreProperties(ignoreUnknown = true)
data class GeneralUserSummaryDTO(
        val firstName: String?,
        val lastName: String?,
        val imageUrl: String?,
        val dislikes: Int,
        val likes: Int,
        val isApproved: Boolean,
        val _id: ObjectId,
        val badges: Array<UserBadgeDTO>
) {
    fun toGeneralUserSummaryViewDTO() = GeneralUserSummaryViewDTO(
            firstName = firstName,
            lastName = lastName,
            imageUrl = imageUrl,
            dislikes = dislikes,
            likes = likes,
            isApproved = isApproved,
            id = _id.toHexString(),
            badges = badges
    )
}