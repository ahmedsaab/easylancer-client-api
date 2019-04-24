package com.easylancer.api.controllers

import com.easylancer.api.data.DataAPIClient
import com.easylancer.api.data.EventEmitter
import com.easylancer.api.data.dto.*
import com.easylancer.api.dto.*
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import kotlinx.coroutines.*

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.web.server.ResponseStatusException


@RequestMapping("/profiles")
@RestController
@FlowPreview
class ProfilePageController(
        @Autowired private val eventEmitter: EventEmitter,
        @Autowired private val dataClient: DataAPIClient,
        @Autowired private val currentUserId: String
) {
    private var mapper: ObjectMapper = jacksonObjectMapper();

    @GetMapping("/{id}/view")
    suspend fun viewProfile(@PathVariable("id") id: String) : ViewProfileDTO {
        val user: UserDTO = dataClient.getUser(id)

        return user.toViewProfileDTO();
    }

    @PutMapping("/{id}/edit")
    suspend fun updateProfile(
            @PathVariable("id") id: String,
            @RequestBody profileDto: UpdateProfileDTO
    ) : IdViewDTO {
        val profileBody = mapper.valueToTree<ObjectNode>(profileDto)

        if(id == currentUserId) {
            dataClient.putUser(id, profileBody)
        } else {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Cannot update this profile")
        }

        return IdViewDTO(id);
    }

    @GetMapping("/{id}/tasks/finished")
    suspend fun listUserAssignedTasks(@PathVariable("id") id: String) : List<ListViewTaskDTO> {
        val tasks: Array<TaskDTO> = dataClient.getUserFinishedTasks(id);

        return tasks.map { it.toListViewTaskDTO() }
    }

    @GetMapping("/{id}/tasks/created")
    suspend fun listUserCreatedTasks(@PathVariable("id") id: String) : List<ListViewTaskDTO> {
        val tasks: Array<TaskDTO> = dataClient.getUserCreatedTasks(id);

        return tasks.map { it.toListViewTaskDTO() }
    }

    @GetMapping("/{id}/reviews")
    suspend fun listUserTaskReviews(@PathVariable("id") id: String): List<ListViewTaskRatingDTO> {
        val tasks: Array<FullTaskRatingDTO> = dataClient.getUserReviews(id);

        return tasks.map { it.toListViewTaskRatingDTO() }
    }

    // TODO: this is postponed after the release of 1.0.0
    @PostMapping("/{id}/approve")
    suspend fun approveUser(@PathVariable("id") id: String): Unit {

    }

    /** Handle the error */
    @ExceptionHandler(Exception::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun handleError(e: Exception): ObjectNode {
        val resp = JsonNodeFactory.instance.objectNode()
        resp.put("error",e.message)
        resp.put("code",500)
        e.printStackTrace()
        return resp
    }
}