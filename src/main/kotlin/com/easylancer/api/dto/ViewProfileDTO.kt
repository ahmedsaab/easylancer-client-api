package com.easylancer.api.dto

import com.easylancer.api.data.dto.inbound.UserBadgeDTO
import com.easylancer.api.data.dto.inbound.UserLocationDTO
import com.easylancer.api.data.dto.inbound.UserRatingsDTO
import com.easylancer.api.data.dto.inbound.UserTagDTO
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import java.util.*

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder(alphabetic=true)
data class ViewProfileDTO(
        val about: String,
        val createdTasksCount: Int,
        val finishedTasksCount: Int,
        val lastName: String,
        val firstName: String,
        val imageUrl: String?,
        val imagesUrls: Array<String>,
        val languages: Array<String>,
        val isApproved: Boolean,
        val ratings: UserRatingsDTO,
        val id: String,
        val lastSeen: Date,
        val location: UserLocationDTO?,
        val createdAt: Date,
        val badges: Array<UserBadgeDTO>,
        val tags: Array<UserTagDTO>
)