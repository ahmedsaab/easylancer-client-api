package com.easylancer.api.dto

import com.easylancer.api.data.dto.TaskLocationDTO
import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import java.util.*

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = false)
data class UpdateTaskDTO(
    val paymentMethod: String?,
    val description: String?,
    val title: String?,
    @JsonFormat(shape= JsonFormat.Shape.NUMBER_INT)
    val price: Int?,
    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    val endDateTime: Date?,
    val imagesUrls: Array<String>?,
    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    val startDateTime: Date?,
    val location: TaskLocationDTO?
)