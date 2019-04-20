package com.easylancer.api.dto

import com.easylancer.api.data.dto.TaskLocationDTO
import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonInclude
import java.util.*

@JsonInclude(JsonInclude.Include.NON_NULL)
data class CreateOfferDTO(
    val paymentMethod: String?,
    @JsonFormat(shape= JsonFormat.Shape.NUMBER_INT)
    val timeToLive: Int?,
    val notifyCreator: Boolean?,
    val message: String,
    @JsonFormat(shape= JsonFormat.Shape.NUMBER_INT)
    val price: Int
)