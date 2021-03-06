package com.easylancer.api.data.dto.inbound

import com.easylancer.api.exceptions.runtime.TransformationException
import com.easylancer.api.dto.ViewProfileDTO
import com.easylancer.api.dto.ViewSettingsDTO
import com.easylancer.api.dto.ViewUserDTO
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.bson.types.ObjectId
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
        val imagesUrls: Array<String>,
        val phoneNumber: String?,
        val isApproved: Boolean,
        val gender: String,
        val ratings: UserRatingsDTO,
        val settings: UserSettingsDTO,
        val _id: ObjectId,
        val auth: String,
        val lastSeen: Date,
        val languages: Array<String>,
        val createdAt: Date,
        val location: UserLocationDTO?,
        val birthDate: Date?,
        val city: String?,
        val badges: Array<UserBadgeDTO>,
        val tags: Array<UserTagDTO>
) {
    fun toViewProfileDTO() = ViewProfileDTO(
        about = about,
        createdTasksCount = createdTasks.size,
        finishedTasksCount = finishedTasks.size,
        lastName = lastName ?: throw TransformationException("lastName of user is null"),
        firstName = firstName ?: throw TransformationException("firstName of user is null"),
        languages = languages,
        location = location,
        imageUrl = imageUrl,
        imagesUrls = imagesUrls,
        isApproved = isApproved,
        ratings = ratings,
        lastSeen = lastSeen,
        badges = badges,
        id = _id.toHexString(),
        createdAt = createdAt,
        tags = tags
    )
    fun toViewUserDTO() = ViewUserDTO(
        lastName = lastName,
        firstName = firstName,
        imageUrl = imageUrl ,
        gender = gender,
        city =  city,
        id = _id.toHexString(),
        birthDate = birthDate,
        phoneNumber = phoneNumber,
        settings = settings
    )
    fun toViewSettingsDTO() = ViewSettingsDTO(
        lastName = lastName ?: throw TransformationException("lastName of user is null"),
        firstName = firstName ?: throw TransformationException("firstName of user is null"),
        gender = gender,
        id = _id.toHexString(),
        birthDate = birthDate,
        phoneNumber = phoneNumber,
        settings = settings,
        location = location,
        isApproved = isApproved
    )
}