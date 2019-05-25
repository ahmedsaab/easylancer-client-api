package com.easylancer.api.controllers

import com.easylancer.api.data.EventEmitter
import com.easylancer.api.data.dto.*
import com.easylancer.api.data.blocking.exceptions.DataApiNotFoundException
import com.easylancer.api.dto.*
import com.easylancer.api.exceptions.http.HttpAuthorizationException
import com.easylancer.api.exceptions.http.HttpNotFoundException
import com.easylancer.api.security.User
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RequestMapping("/profiles")
@RestController
class ProfilePageController(
        @Autowired val eventEmitter: EventEmitter,
        @Autowired private val bClient: com.easylancer.api.data.blocking.DataApiClient
) {
    private val mapper: ObjectMapper = jacksonObjectMapper()

    @GetMapping("/{id}/view")
    suspend fun viewProfile(@PathVariable("id") id: String) : ViewProfileDTO {
        try {
            val user: UserDTO = bClient.getUser(id)

            return user.toViewProfileDTO();
        } catch (e: DataApiNotFoundException) {
            throw HttpNotFoundException("No profile found with this id", e)
        }
    }

    @PutMapping("/{id}/edit")
    suspend fun updateProfile(
            @PathVariable("id") id: String,
            @RequestBody profileDto: UpdateProfileDTO,
            @AuthenticationPrincipal user: User
    ) : IdViewDTO {
        val profileBody = mapper.valueToTree<ObjectNode>(profileDto)

        if(id == user.id) {
            bClient.putUser(id, profileBody)
        } else {
            throw HttpAuthorizationException("Cannot update this profile")
        }

        return IdViewDTO(id);
    }

    @GetMapping("/{id}/tasks/finished")
    suspend fun listUserAssignedTasks(@PathVariable("id") id: String) : List<ListViewTaskDTO> {
        val tasks: Array<TaskDTO> = bClient.getUserFinishedTasks(id);

        return tasks.map { it.toListViewTaskDTO() }
    }

    @GetMapping("/{id}/tasks/created")
    suspend fun listUserCreatedTasks(@PathVariable("id") id: String) : List<ListViewTaskDTO> {
        val tasks: Array<TaskDTO> = bClient.getUserCreatedTasks(id);

        return tasks.map { it.toListViewTaskDTO() }
    }

    @GetMapping("/{id}/reviews")
    suspend fun listUserTaskReviews(@PathVariable("id") id: String): List<ListViewTaskRatingDTO> {
        val tasks: Array<FullTaskRatingDTO> = bClient.getUserReviews(id);

        return tasks.map { it.toListViewTaskRatingDTO() }
    }

    // TODO: this is postponed after the release of 1.0.0
    @PostMapping("/{id}/approve")
    suspend fun approveUser(@PathVariable("id") id: String): Unit {

    }
}