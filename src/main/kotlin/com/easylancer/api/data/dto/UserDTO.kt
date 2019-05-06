package com.easylancer.api.data.dto

import com.easylancer.api.exceptions.TransformationException
import com.easylancer.api.dto.ViewProfileDTO
import com.easylancer.api.dto.ViewUserDTO
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.util.*

@JsonIgnoreProperties(ignoreUnknown = true)
data class UserDTO(
    val about: String,
    val createdTasks: Array<String>,
    val acceptedTasks: Array<String>,
    val appliedTasks: Array<String>,
    val finishedTasks: Array<String>,
    val lastName: String?,
    val firstName: String?,
    val imageUrl: String?,
    val phoneNumber: String?,
    val dislikes: Int,
    val likes: Int,
    val isApproved: Boolean,
    val gender: String,
    val ratings: UserRatingDTO,
    val settings: UserSettingsDTO,
    val _id: String,
    val email: String,
    val lastSeen: Date,
    val createdAt: Date,
    val birthDate: Date?,
    val city: String?,
    val badges: Array<UserBadgeDTO>
) {
    fun toViewProfileDTO() = ViewProfileDTO(
        about = about,
        createdTasksCount = createdTasks.size,
        finishedTasksCount = finishedTasks.size,
        lastName = lastName ?: throw TransformationException("lastName of user is null"),
        firstName = firstName ?: throw TransformationException("firstName of user is null"),
        imageUrl = imageUrl,
        dislikes = dislikes,
        likes = likes,
        isApproved = isApproved,
        ratings = ratings,
        lastSeen = lastSeen,
        badges = badges,
        id = _id,
        createdAt = createdAt
    )
    fun toViewUserDTO() = ViewUserDTO(
        lastName = lastName,
        firstName = firstName,
        imageUrl = imageUrl ,
        gender = gender,
        city =  city,
        id = _id,
        email = email,
        birthDate = birthDate,
        phoneNumber = phoneNumber,
        settings = settings
    )
}