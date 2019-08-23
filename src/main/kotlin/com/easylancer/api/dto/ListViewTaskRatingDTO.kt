package com.easylancer.api.dto

import com.easylancer.api.data.dto.RatingCriteriaDTO
import com.fasterxml.jackson.annotation.JsonPropertyOrder

@JsonPropertyOrder(alphabetic=true)
data class ListViewTaskRatingDTO(
        val creatorUser: GeneralUserSummaryViewDTO,
        val criteria: RatingCriteriaDTO,
        val description: String,
        val like: Boolean
)