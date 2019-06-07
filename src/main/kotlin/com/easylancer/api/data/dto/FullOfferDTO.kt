package com.easylancer.api.data.dto

import com.easylancer.api.dto.UserSummaryViewDTO
import com.easylancer.api.dto.ViewOfferDTO
import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.bson.types.ObjectId

/**
 * Representation of a Task
 * @property username The username of the user
 * @property screenName The screen name of the user
 * @property email The email address of the user
 * @property registered When the user registered with us
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class FullOfferDTO(
        val _id : ObjectId,
        val paymentMethod: String,
        @JsonFormat(shape= JsonFormat.Shape.NUMBER_INT)
    val timeToLive: Int,
        val notifyCreator: Boolean,
        val message: String,
        @JsonFormat(shape= JsonFormat.Shape.NUMBER_INT)
    val price: Int,
        val workerUser: UserSummaryDTO
) {
    fun toViewOfferDTO() = ViewOfferDTO(
        id = _id.toHexString(),
        paymentMethod = paymentMethod,
        message = message,
        price = price,
        workerUser = UserSummaryViewDTO(
                id = workerUser._id.toHexString(),
                firstName = workerUser.firstName,
                lastName = workerUser.lastName,
                imageUrl = workerUser.imageUrl,
                likes = workerUser.likes,
                dislikes = workerUser.dislikes,
                isApproved = workerUser.isApproved,
                badges = workerUser.badges
        )
    )
}