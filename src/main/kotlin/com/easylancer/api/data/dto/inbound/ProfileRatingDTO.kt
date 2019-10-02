package com.easylancer.api.data.dto.inbound

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class ProfileRatingDTO(
    val likes: Int,
    val dislikes: Int,
    val value: Int,
    val count: Int
)