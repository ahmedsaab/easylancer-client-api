package com.easylancer.api.data.dto.inbound

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class TaskLocationDTO(
        val geo: GeoLocationDTO,
        val country: String,
        val city: String,
        val address: String
)