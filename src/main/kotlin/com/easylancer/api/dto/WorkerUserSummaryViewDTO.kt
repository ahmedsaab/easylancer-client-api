package com.easylancer.api.dto

import com.easylancer.api.data.dto.inbound.UserBadgeDTO
import com.easylancer.api.data.dto.inbound.UserRatingsDTO
import com.easylancer.api.data.dto.inbound.UserTagDTO
import com.fasterxml.jackson.annotation.JsonPropertyOrder

@JsonPropertyOrder(alphabetic=true)
data class WorkerUserSummaryViewDTO(
        val firstName: String?,
        val lastName: String?,
        val imageUrl: String?,
        val isApproved: Boolean,
        val id: String,
        val badges: Array<UserBadgeDTO>,
        val tags: Array<UserTagDTO>,
        val ratings: UserRatingsDTO
)