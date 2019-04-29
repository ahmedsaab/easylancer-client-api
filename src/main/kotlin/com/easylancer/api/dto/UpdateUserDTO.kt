package com.easylancer.api.dto

import com.easylancer.api.data.dto.UserSettingsDTO
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.util.*

@JsonIgnoreProperties(ignoreUnknown = false)
data class UpdateUserDTO(
        val lastName: String?,
        val firstName: String?,
        val imageUrl: String?,
        val gender: String?,
        val city: String?,
        val email: String?,
        val birthDate: Date?,
        val phoneNumber: String?,
        val settings: UserSettingsDTO?
)