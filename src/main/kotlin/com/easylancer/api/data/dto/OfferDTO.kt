package com.easylancer.api.data.dto

import com.easylancer.api.dto.IdViewDTO
import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIgnoreProperties

/**
 * Representation of a Task
 * @property username The username of the user
 * @property screenName The screen name of the user
 * @property email The email address of the user
 * @property registered When the user registered with us
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class OfferDTO(
    val _id : String,
    val paymentMethod: String,
    @JsonFormat(shape= JsonFormat.Shape.NUMBER_INT)
    val timeToLive: Int,
    val notifyCreator: Boolean,
    val message: String,
    @JsonFormat(shape= JsonFormat.Shape.NUMBER_INT)
    val price: Int
) {
    fun toIdDTO() = IdViewDTO(
        id = _id
    )
}