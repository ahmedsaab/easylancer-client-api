package com.easylancer.api.dto

import com.easylancer.api.data.dto.TaskLocationDTO
import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import java.util.*

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = false)
data class CreateUserDTO(
        val email: String,
        val firstName: String,
        val lastName: String,
        val password: String
)