package com.easylancer.api

import com.fasterxml.jackson.databind.node.ObjectNode
import java.time.Instant

/**
 * Representation of a Task
 * @property username The username of the user
 * @property screenName The screen name of the user
 * @property email The email address of the user
 * @property registered When the user registered with us
 */
data class DataApiResponseDTO(
        val version: String,
        val data: TaskDTO
)