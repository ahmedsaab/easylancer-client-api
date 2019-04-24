package com.easylancer.api.data.dto

import com.easylancer.api.dto.DetailViewTaskDTO
import com.easylancer.api.dto.UserSummaryViewDTO
import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class UserSummaryDTO(
        val firstName: String?,
        val lastName: String?,
        val imageUrl: String?,
        val dislikes: Int,
        val likes: Int,
        val isApproved: Boolean,
        val _id: String,
        val badges: Array<UserBadgeDTO>
) {
    fun toUserSummaryViewDTO() = UserSummaryViewDTO(
            firstName = firstName,
            lastName = lastName,
            imageUrl = imageUrl,
            dislikes = dislikes,
            likes = likes,
            isApproved = isApproved,
            id = _id,
            badges = badges
    )
}