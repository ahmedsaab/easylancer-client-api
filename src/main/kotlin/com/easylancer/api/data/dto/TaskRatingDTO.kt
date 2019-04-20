package com.easylancer.api.data.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class TaskRatingDTO(
        val criteria: RatingCriteriaDTO,
        val description: String,
        val like: Boolean
)