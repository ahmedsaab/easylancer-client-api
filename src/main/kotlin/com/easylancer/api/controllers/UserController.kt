package com.easylancer.api.controllers

import com.easylancer.api.data.EventEmitter
import com.easylancer.api.data.dto.*
import com.easylancer.api.data.reactive.exceptions.DataApiNotFoundException
import com.easylancer.api.dto.*
import com.easylancer.api.exceptions.http.HttpAuthorizationException
import com.easylancer.api.exceptions.http.HttpNotFoundException
import com.easylancer.api.security.User
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import reactor.core.publisher.Mono


@RequestMapping("/users")
@RestController
class UserController(
        @Autowired private val eventEmitter: EventEmitter,
        @Autowired private val bClient: com.easylancer.api.data.blocking.DataApiClient,
        @Autowired private val rClient: com.easylancer.api.data.reactive.DataApiClient
) {
    private var mapper: ObjectMapper = jacksonObjectMapper();

    @GetMapping("/{id}/view")
    @PreAuthorize("#id.equals(#user.id)")
    fun viewUser(
            @PathVariable("id") id: String,
            @AuthenticationPrincipal user: User
    ) : Mono<ViewUserDTO> {
        return rClient.getUser(id)
            .map { userDto ->
                userDto.toViewUserDTO()
            }.onErrorMap {e ->
                if (e is DataApiNotFoundException)
                    HttpNotFoundException("no user found with this id", e)
                else e
            }
    }



    @GetMapping("/{id}/tasks")
    @PreAuthorize("#id.equals(#user.id)")
    fun getUserRelatedTasks(
            @PathVariable("id") id: String,
            @AuthenticationPrincipal user: User
    ) : MyTasksViewDTO {
        val tasks: Array<TaskDTO> = bClient.getUserRelatedTasks(id);
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
    @PreAuthorize("#id.equals(#user.id)")
    fun updateUser(
            @PathVariable("id") id: String,
            @RequestBody userDto: UpdateUserDTO,
            @AuthenticationPrincipal user: User
    ) : IdViewDTO {
        if(id != user.id) {
            throw HttpAuthorizationException("Cannot edit this user")
        }
        val userBody = mapper.valueToTree<ObjectNode>(userDto)
        bClient.putUser(id, userBody)

        return IdViewDTO(id);
    }
}