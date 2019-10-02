package com.easylancer.api.data.dto.inbound

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class UserLocationDTO(
        val country: String?,
        val city: String?
)