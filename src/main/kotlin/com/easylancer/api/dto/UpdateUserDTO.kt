package com.easylancer.api.dto

import com.easylancer.api.data.dto.inbound.UserSettingsDTO
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import java.util.*

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = false)
data class UpdateUserDTO(
        val lastName: String?,
        val firstName: String?,
        val imageUrl: String?,
        val gender: String?,
        val city: String?,
        val birthDate: Date?,
        val phoneNumber: String?,
        val settings: UserSettingsDTO?
)