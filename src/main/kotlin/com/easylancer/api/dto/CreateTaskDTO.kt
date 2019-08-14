package com.easylancer.api.dto

import com.easylancer.api.data.dto.TaskLocationDTO
import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import java.util.*

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = false)
data class CreateTaskDTO(
    val category: String,
    val type: String,
    val paymentMethod: String?,
    val description: String,
    val title: String,
    @JsonFormat(shape= JsonFormat.Shape.NUMBER_INT)
    val price: Int,
    val imagesUrls: Array<String>,
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone="UTC")
    val startDateTime: Date,
    val location: TaskLocationDTO
)