package com.easylancer.api.data.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class UserRatingDTO(
        val measure1: String,
        val measure2: String
)