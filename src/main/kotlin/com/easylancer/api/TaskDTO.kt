package com.easylancer.api

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.node.ObjectNode
import java.time.Instant

/**
 * Representation of a Task
 * @property username The username of the user
 * @property screenName The screen name of the user
 * @property email The email address of the user
 * @property registered When the user registered with us
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class TaskDTO(
        val category: String,
        val type: String,
        val paymentMethod: String,
        val description: String,
        val title: String
)