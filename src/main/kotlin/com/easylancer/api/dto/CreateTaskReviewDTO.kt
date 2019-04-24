package com.easylancer.api.dto

import com.easylancer.api.data.dto.RatingCriteriaDTO
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = false)
data class CreateTaskReviewDTO(
        val criteria: RatingCriteriaDTO,
        val description: String,
        val like: Boolean
)