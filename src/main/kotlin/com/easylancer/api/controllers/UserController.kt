package com.easylancer.api.controllers

import com.easylancer.api.data.DataApiClient
import com.easylancer.api.data.exceptions.DataApiBadRequestException
import com.easylancer.api.data.exceptions.DataApiNotFoundException
import com.easylancer.api.exceptions.http.HttpBadRequestException
import com.easylancer.api.exceptions.http.HttpNotFoundException
import com.easylancer.api.security.UserPrincipal
import com.easylancer.api.dto.*
import org.bson.types.ObjectId

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import reactor.core.publisher.Mono
import reactor.core.publisher.onErrorMap

@RequestMapping("/users")
@RestController
class UserController(
        @Autowired private val client: DataApiClient
) {
    @GetMapping("/{id}/view")
    @PreAuthorize("hasAuthority('user:read:' + #id)")
    fun viewUser(
            @PathVariable("id") id: ObjectId,
            @AuthenticationPrincipal user: UserPrincipal
    ) : Mono<ViewUserDTO> {
        return client.getUser(id).map { userDto ->
                userDto.toViewUserDTO()
            }.onErrorMap(DataApiNotFoundException::class) { e ->
                HttpNotFoundException("no user found with this id", e)
            }
    }

    @GetMapping("/{id}/tasks")
    @PreAuthorize("hasAuthority('user:read:' + #id)")
    fun getUserRelatedTasks(
            @PathVariable("id") id: ObjectId,
            @AuthenticationPrincipal user: UserPrincipal
    ) : Mono<MyTasksViewDTO> {
        return client.getUserRelatedTasks(id).collect({ MyTasksViewDTO() }, {
            container, task ->
                val dto = task.toListViewTaskDTO()

                if (task.workerUser == user.id) {
                    if (task.status == "done" || task.status == "not-done") {
                        container.finished += dto
                    } else {
                        container.assigned += dto
                    }
                } else if (task.creatorUser._id == user.id) {
                    container.created += dto
                } else {
                    container.applied += dto
                }
            }
        )
    }

    @PutMapping("/{id}/edit")
    @PreAuthorize("hasAuthority('user:edit:' + #id)")
    fun updateUser(
            @PathVariable("id") id: ObjectId,
            @RequestBody userDto: UpdateUserDTO,
            @AuthenticationPrincipal user: UserPrincipal
    ) : Mono<ViewUserDTO> {
        return client.putUser(id, userDto).map { u ->
            u.toViewUserDTO()
        }.onErrorMap(DataApiBadRequestException::class) { e ->
            HttpBadRequestException("Sorry can't do, please send a valid user data change!", e, e.invalidParams)
        }
    }
}