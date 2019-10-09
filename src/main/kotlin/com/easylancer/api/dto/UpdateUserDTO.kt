package com.easylancer.api.dto

import com.easylancer.api.data.dto.inbound.UserLocationDTO
import com.easylancer.api.data.dto.inbound.UserSettingsDTO
import com.fasterxml.jackson.annotation.JsonFormat
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
        val location: UserLocationDTO?,
        @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone="UTC")
        val birthDate: Date?,
        val phoneNumber: String?,
        val settings: UserSettingsDTO?
)