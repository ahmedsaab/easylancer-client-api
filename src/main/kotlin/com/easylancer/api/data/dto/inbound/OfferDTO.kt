package com.easylancer.api.data.dto.inbound

import com.easylancer.api.dto.IdViewDTO
import com.easylancer.api.dto.OfferSummaryViewDTO
import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.bson.types.ObjectId

@JsonIgnoreProperties(ignoreUnknown = true)
data class OfferDTO(
        val _id : ObjectId,
        val paymentMethod: String,
        @JsonFormat(shape= JsonFormat.Shape.NUMBER_INT)
        val timeToLive: Int,
        val notifyCreator: Boolean,
        val message: String,
        @JsonFormat(shape= JsonFormat.Shape.NUMBER_INT)
        val price: Int,
        val workerUser: ObjectId
) {
    fun toIdDTO() = IdViewDTO(
        id = _id.toHexString()
    )

    fun toOfferSummaryViewDTO() = OfferSummaryViewDTO(
        id = _id.toHexString(),
        paymentMethod = paymentMethod,
        message = message,
        price = price
    )
}