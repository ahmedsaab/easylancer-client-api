package com.easylancer.api.data.dto.inbound

import com.easylancer.api.dto.GeneralUserSummaryViewDTO
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.bson.types.ObjectId

@JsonIgnoreProperties(ignoreUnknown = true)
data class GeneralUserSummaryDTO(
        val firstName: String?,
        val lastName: String?,
        val imageUrl: String?,
        val ratings: UserRatingsDTO,
        val isApproved: Boolean,
        val _id: ObjectId,
        val badges: Array<UserBadgeDTO>
) {
    fun toGeneralUserSummaryViewDTO() = GeneralUserSummaryViewDTO(
            firstName = firstName,
            lastName = lastName,
            imageUrl = imageUrl,
            ratings = ratings,
            isApproved = isApproved,
            id = _id.toHexString(),
            badges = badges
    )
}