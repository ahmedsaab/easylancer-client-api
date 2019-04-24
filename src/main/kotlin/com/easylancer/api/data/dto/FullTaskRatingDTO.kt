package com.easylancer.api.data.dto

import com.easylancer.api.dto.ListViewTaskRatingDTO
import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class FullTaskRatingDTO(
    val creatorUser: UserSummaryDTO,
    val criteria: RatingCriteriaDTO,
    val description: String,
    val like: Boolean
) {
    fun toListViewTaskRatingDTO() = ListViewTaskRatingDTO(
            creatorUser = creatorUser,
            criteria = criteria,
            description = description,
            like = like
    )
}