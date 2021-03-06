package com.easylancer.api.dto
import com.easylancer.api.data.dto.inbound.TaskLocationDTO
import com.easylancer.api.data.dto.types.TaskStatus
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import java.util.*

@JsonPropertyOrder(alphabetic=true)
data class AppliedViewTaskDTO(
        val category: String,
        val type: String,
        val paymentMethod: String,
        val title: String,
        val price: Int,
        val status: TaskStatus,
        val id: String,
        val tags: Array<String>,
        val startDateTime: Date,
        val location: TaskLocationDTO,
        val createdAt: Date,
        val creatorUser: GeneralUserSummaryViewDTO,
        val offer: OfferSummaryViewDTO
)