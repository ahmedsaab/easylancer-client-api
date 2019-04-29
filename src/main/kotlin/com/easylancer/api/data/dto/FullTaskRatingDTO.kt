package com.easylancer.api.data.dto

import com.easylancer.api.dto.ListViewTaskRatingDTO
import com.easylancer.api.dto.UserSummaryViewDTO
import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class FullTaskRatingDTO(
    val creatorUser: UserSummaryDTO,
    val criteria: RatingCriteriaDTO,
    val description: String,
    val like: Boolean
) {
    fun toListViewTaskRatingDTO() = ListViewTaskRatingDTO(
            creatorUser = UserSummaryViewDTO(
                    firstName = creatorUser.firstName,
                    lastName = creatorUser.lastName,
                    imageUrl = creatorUser.imageUrl,
                    likes = creatorUser.likes,
                    dislikes = creatorUser.dislikes,
                    badges = creatorUser.badges,
                    isApproved = creatorUser.isApproved,
                    id = creatorUser._id
            ),
            criteria = criteria,
            description = description,
            like = like
    )
}