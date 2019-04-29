package com.easylancer.api.dto

import com.easylancer.api.data.dto.UserSettingsDTO
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import java.util.*

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder(alphabetic=true)
data class ViewUserDTO(
        val lastName: String?,
        val firstName: String?,
        val imageUrl: String?,
        val gender: String?,
        val city: String?,
        val id: String,
        val email: String,
        val birthDate: Date?,
        val phoneNumber: String?,
        val settings: UserSettingsDTO
)