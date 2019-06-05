package com.easylancer.api.controllers

import com.easylancer.api.data.DataApiClient
import com.easylancer.api.data.exceptions.DataApiNotFoundException
import com.easylancer.api.dto.*
import com.easylancer.api.exceptions.http.HttpNotFoundException
import com.easylancer.api.security.UserPrincipal
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RequestMapping("/profiles")
@RestController
class ProfilePageController(
        @Autowired private val client: DataApiClient
) {
    private val mapper: ObjectMapper = jacksonObjectMapper()

    @GetMapping("/{id}/view")
    fun viewProfile(@PathVariable("id") id: String) : Mono<ViewProfileDTO> {
        return client.getUser(id).map {
            it.toViewProfileDTO()
        }.onErrorMap { e ->
            if (e is DataApiNotFoundException)
                HttpNotFoundException("no user found with this id", e)
            else e
        }
    }

    @PutMapping("/{id}/edit")
    @PreAuthorize("hasAuthority('user:edit:' + #id)")
    fun updateProfile(
            @PathVariable("id") id: String,
            @RequestBody profileDto: UpdateProfileDTO,
            @AuthenticationPrincipal user: UserPrincipal
    ) : Mono<ViewProfileDTO> {
        return client.putUser(id, profileDto).map {
            it.toViewProfileDTO()
        }.onErrorMap { e ->
            if (e is DataApiNotFoundException)
                HttpNotFoundException("no user found with this id", e)
            else e
        }
    }

    @GetMapping("/{id}/tasks/finished")
    fun listUserAssignedTasks(@PathVariable("id") id: String) : Flux<ListViewTaskDTO> {
        return client.getUserFinishedTasks(id).map {
            it.toListViewTaskDTO()
        };
    }

    @GetMapping("/{id}/tasks/created")
    fun listUserCreatedTasks(@PathVariable("id") id: String): Flux<ListViewTaskDTO> {
        return client.getUserCreatedTasks(id).map {
            it.toListViewTaskDTO()
        };
    }

    @GetMapping("/{id}/reviews")
    fun listUserTaskReviews(@PathVariable("id") id: String): Flux<ListViewTaskRatingDTO> {
        return client.getUserReviews(id).map {
            it.toListViewTaskRatingDTO()
        };
    }

    // TODO: this is postponed after the release of 1.0.0
    @PostMapping("/{id}/approve")
    fun approveUser(@PathVariable("id") id: String): Unit {

    }
}