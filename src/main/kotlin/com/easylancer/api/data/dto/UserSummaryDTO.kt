package com.easylancer.api.data.dto

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
)