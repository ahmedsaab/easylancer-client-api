package com.easylancer.api.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonPropertyOrder

data class MyTasksViewDTO(
        val new: MutableList<ListViewTaskDTO> = mutableListOf(),
        val planned: MutableList<ListViewTaskDTO> = mutableListOf(),
        val finished: MutableList<ListViewTaskDTO> = mutableListOf(),
        val cancelled: MutableList<ListViewTaskDTO> = mutableListOf()
)