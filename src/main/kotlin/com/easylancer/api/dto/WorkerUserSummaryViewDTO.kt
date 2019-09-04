package com.easylancer.api.dto

import com.easylancer.api.data.dto.UserBadgeDTO
import com.easylancer.api.data.dto.UserTagDTO
import com.fasterxml.jackson.annotation.JsonPropertyOrder

@JsonPropertyOrder(alphabetic=true)
data class WorkerUserSummaryViewDTO(
        val firstName: String?,
        val lastName: String?,
        val imageUrl: String?,
        val dislikes: Int,
        val likes: Int,
        val isApproved: Boolean,
        val id: String,
        val badges: Array<UserBadgeDTO>,
        val tags: Array<UserTagDTO>
)