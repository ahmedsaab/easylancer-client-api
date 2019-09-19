package com.easylancer.api.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = false)
data class CreateUserDTO(
        val email: String,
        val firstName: String,
        val lastName: String,
        val password: String
)