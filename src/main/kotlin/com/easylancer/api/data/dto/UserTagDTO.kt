package com.easylancer.api.data.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class UserTagDTO(
        val value: String,
        val count: Number
)