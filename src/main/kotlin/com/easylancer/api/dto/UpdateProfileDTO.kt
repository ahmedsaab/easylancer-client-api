package com.easylancer.api.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = false)
data class UpdateProfileDTO(
    val about: String?
)