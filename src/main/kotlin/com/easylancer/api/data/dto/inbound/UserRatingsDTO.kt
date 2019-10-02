package com.easylancer.api.data.dto.inbound

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class UserRatingsDTO(
        val creator: ProfileRatingDTO,
        val worker: ProfileRatingDTO
)