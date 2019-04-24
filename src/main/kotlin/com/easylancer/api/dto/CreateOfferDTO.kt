package com.easylancer.api.dto

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = false)
data class CreateOfferDTO(
    val paymentMethod: String?,
    @JsonFormat(shape= JsonFormat.Shape.NUMBER_INT)
    val timeToLive: Int?,
    val notifyCreator: Boolean?,
    val message: String,
    @JsonFormat(shape= JsonFormat.Shape.NUMBER_INT)
    val price: Int
)