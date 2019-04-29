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


@RequestMapping("/users")
@RestController
@FlowPreview
class UserController(
        @Autowired private val eventEmitter: EventEmitter,
        @Autowired private val dataClient: DataAPIClient,
        @Autowired private val currentUserId: String
) {
    private var mapper: ObjectMapper = jacksonObjectMapper();

    @GetMapping("/{id}/view")
    suspend fun viewUser(@PathVariable("id") id: String) : ViewUserDTO {
        if(id != currentUserId) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Cannot view this user")
        }
        val user: UserDTO = dataClient.getUser(id)

        return user.toViewUserDTO()
    }

    @GetMapping("/{id}/tasks")
    suspend fun getUserRelatedTasks(@PathVariable("id") id: String) : MyTasksViewDTO {
        val tasks: Array<TaskDTO> = dataClient.getUserRelatedTasks(id);
        val myTasksView = MyTasksViewDTO()

        tasks.forEach {
            val task = it.toListViewTaskDTO()

            if (it.workerUser == currentUserId) {
                if ((it.status == "done" || it.status == "not-done")) {
                    myTasksView.finished += task
                } else {
                    myTasksView.assigned += task
                }
            } else if (it.creatorUser === currentUserId) {
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
            @RequestBody userDto: UpdateUserDTO
    ) : IdViewDTO {
        if(id != currentUserId) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Cannot update this user")
        }
        val userBody = mapper.valueToTree<ObjectNode>(userDto)
        dataClient.putUser(id, userBody)

        return IdViewDTO(id);
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