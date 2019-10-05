package com.easylancer.api.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude

@JsonIgnoreProperties(ignoreUnknown = false)
data class UpdateProfileDTO(
    val about: String?,
    val languages: Array<String>?,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val imagesUrls: Array<String>?,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val imageUrl: String?
)