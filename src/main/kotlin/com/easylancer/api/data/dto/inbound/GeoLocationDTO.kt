package com.easylancer.api.data.dto.inbound

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class GeoLocationDTO(
        val lat: Float,
        val lng: Float
)
