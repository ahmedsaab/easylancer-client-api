package com.easylancer.api.dto

import com.easylancer.api.data.dto.inbound.UserBadgeDTO
import com.easylancer.api.data.dto.inbound.UserRatingsDTO
import com.fasterxml.jackson.annotation.JsonPropertyOrder

@JsonPropertyOrder(alphabetic=true)
data class GeneralUserSummaryViewDTO(
        val firstName: String?,
        val lastName: String?,
        val imageUrl: String?,
        val ratings: UserRatingsDTO,
        val isApproved: Boolean,
        val id: String,
        val badges: Array<UserBadgeDTO>
)