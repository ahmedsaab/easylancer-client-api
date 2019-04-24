package com.easylancer.api.data.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class RatingCriteriaDTO(
        val measure1: Int,
        val measure2: Int
)