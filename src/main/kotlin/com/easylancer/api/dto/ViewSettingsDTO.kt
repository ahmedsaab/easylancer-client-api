package com.easylancer.api.dto

import com.easylancer.api.data.dto.inbound.*
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import java.util.*

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder(alphabetic=true)
data class ViewSettingsDTO(
        val lastName: String,
        val firstName: String,
        val isApproved: Boolean,
        val id: String,
        val location: UserLocationDTO?,
        val birthDate: Date?,
        val settings: UserSettingsDTO,
        val gender: String,
        val phoneNumber: String?
)