package com.easylancer.api.dto

import com.easylancer.api.data.dto.UserBadgeDTO
import com.easylancer.api.data.dto.UserRatingDTO
import com.easylancer.api.data.dto.UserTagDTO
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
        val dislikes: Int,
        val likes: Int,
        val isApproved: Boolean,
        val ratings: UserRatingDTO,
        val id: String,
        val lastSeen: Date,
        val createdAt: Date,
        val badges: Array<UserBadgeDTO>,
        val tags: Array<UserTagDTO>
)