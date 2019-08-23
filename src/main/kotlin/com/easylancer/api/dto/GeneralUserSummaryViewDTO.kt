package com.easylancer.api.dto

import com.easylancer.api.data.dto.UserBadgeDTO
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonPropertyOrder

@JsonPropertyOrder(alphabetic=true)
data class GeneralUserSummaryViewDTO(
        val firstName: String?,
        val lastName: String?,
        val imageUrl: String?,
        val dislikes: Int,
        val likes: Int,
        val isApproved: Boolean,
        val id: String,
        val badges: Array<UserBadgeDTO>
)