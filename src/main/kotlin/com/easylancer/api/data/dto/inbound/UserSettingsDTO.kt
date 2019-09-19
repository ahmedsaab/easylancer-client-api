package com.easylancer.api.data.dto.inbound

import com.easylancer.api.data.dto.types.Role
import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class UserSettingsDTO(
        val role: Role?,
        val setting2: String?
)