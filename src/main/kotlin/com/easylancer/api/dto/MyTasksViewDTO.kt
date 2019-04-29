package com.easylancer.api.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonPropertyOrder

@JsonPropertyOrder(alphabetic=true)
data class MyTasksViewDTO(
        val assigned: MutableList<ListViewTaskDTO> = mutableListOf(),
        val created: MutableList<ListViewTaskDTO> = mutableListOf(),
        val applied: MutableList<ListViewTaskDTO> = mutableListOf(),
        val finished: MutableList<ListViewTaskDTO> = mutableListOf()

)