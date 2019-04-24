package com.easylancer.api.dto

import com.easylancer.api.data.dto.RatingCriteriaDTO
import com.easylancer.api.data.dto.UserSummaryDTO
import com.fasterxml.jackson.annotation.JsonPropertyOrder

@JsonPropertyOrder(alphabetic=true)
data class ListViewTaskRatingDTO(
        val creatorUser: UserSummaryDTO,
        val criteria: RatingCriteriaDTO,
        val description: String,
        val like: Boolean
)