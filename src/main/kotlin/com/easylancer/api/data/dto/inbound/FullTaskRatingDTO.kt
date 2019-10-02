package com.easylancer.api.data.dto.inbound

import com.easylancer.api.dto.ListViewTaskRatingDTO
import com.easylancer.api.dto.GeneralUserSummaryViewDTO
import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class FullTaskRatingDTO(
        val creatorUser: GeneralUserSummaryDTO,
        val rating: Int,
        val description: String,
        val like: Boolean
) {
    fun toListViewTaskRatingDTO() = ListViewTaskRatingDTO(
            creatorUser = GeneralUserSummaryViewDTO(
                    firstName = creatorUser.firstName,
                    lastName = creatorUser.lastName,
                    imageUrl = creatorUser.imageUrl,
                    ratings = creatorUser.ratings,
                    badges = creatorUser.badges,
                    isApproved = creatorUser.isApproved,
                    id = creatorUser._id.toHexString()
            ),
            rating = rating,
            description = description,
            like = like
    )
}