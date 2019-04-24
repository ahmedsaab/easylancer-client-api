package com.easylancer.api.data.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class UserSettingsDTO(
        val setting1: String,
        val setting2: String
)