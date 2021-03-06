package com.easylancer.api.dto

import com.fasterxml.jackson.annotation.JsonPropertyOrder

@JsonPropertyOrder(alphabetic=true)
data class ViewOfferDTO(
    val id: String,
    val paymentMethod: String,
    val price: Int,
    val message: String,
    val workerUser: WorkerUserSummaryViewDTO
)