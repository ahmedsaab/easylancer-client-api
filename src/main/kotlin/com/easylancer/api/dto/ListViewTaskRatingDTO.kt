package com.easylancer.api.dto

import com.fasterxml.jackson.annotation.JsonPropertyOrder

@JsonPropertyOrder(alphabetic=true)
data class ListViewTaskRatingDTO(
        val creatorUser: GeneralUserSummaryViewDTO,
        val rating: Int,
        val description: String,
        val like: Boolean
)