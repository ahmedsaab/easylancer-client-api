package com.easylancer.api.controllers

import com.easylancer.api.data.RestClient
import com.easylancer.api.data.EventEmitter
import com.easylancer.api.data.dto.*
import com.easylancer.api.dto.*
import com.easylancer.api.exceptions.http.HttpAuthorizationException
import com.easylancer.api.security.User
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import kotlinx.coroutines.*

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.server.ResponseStatusException


@RequestMapping("/users")
@RestController
class UserController(
        @Autowired private val eventEmitter: EventEmitter,
        @Autowired private val dataClient: RestClient
) {
    private var mapper: ObjectMapper = jacksonObjectMapper();

    @GetMapping("/{id}/view")
    suspend fun viewUser(
            @PathVariable("id") id: String,
            @AuthenticationPrincipal user: User
    ) : ViewUserDTO {
        if(id != user.id) {
            throw HttpAuthorizationException("Cannot view this user")
        }
        val userDto: UserDTO = dataClient.getUser(id)

        return userDto.toViewUserDTO()
    }

    @GetMapping("/{id}/tasks")
    suspend fun getUserRelatedTasks(
            @PathVariable("id") id: String,
            @AuthenticationPrincipal user: User
    ) : MyTasksViewDTO {
        val tasks: Array<TaskDTO> = dataClient.getUserRelatedTasks(id);
        val myTasksView = MyTasksViewDTO()

        tasks.forEach {
            val task = it.toListViewTaskDTO()

            if (it.workerUser == user.id) {
                if ((it.status == "done" || it.status == "not-done")) {
                    myTasksView.finished += task
                } else {
                    myTasksView.assigned += task
                }
            } else if (it.creatorUser === user.id) {
                myTasksView.created += task
            } else {
                myTasksView.applied += task
            }
        }

        return myTasksView
    }

    @PutMapping("/{id}/edit")
    suspend fun updateUser(
            @PathVariable("id") id: String,
            @RequestBody userDto: UpdateUserDTO,
            @AuthenticationPrincipal user: User
    ) : IdViewDTO {
        if(id != user.id) {
            throw HttpAuthorizationException("Cannot edit this user")
        }
        val userBody = mapper.valueToTree<ObjectNode>(userDto)
        dataClient.putUser(id, userBody)

        return IdViewDTO(id);
    }
}