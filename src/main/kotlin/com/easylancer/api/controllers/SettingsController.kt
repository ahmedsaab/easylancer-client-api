package com.easylancer.api.controllers

import com.easylancer.api.data.DataApiClient
import com.easylancer.api.data.dto.types.*
import com.easylancer.api.data.exceptions.DataApiBadRequestException
import com.easylancer.api.data.exceptions.DataApiNotFoundException
import com.easylancer.api.exceptions.http.HttpBadRequestException
import com.easylancer.api.exceptions.http.HttpNotFoundException
import com.easylancer.api.security.UserPrincipal
import com.easylancer.api.dto.*

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import org.springframework.security.core.annotation.AuthenticationPrincipal
import reactor.core.publisher.Mono
import reactor.core.publisher.onErrorMap
import java.lang.Exception

@RequestMapping("/settings")
@RestController
class SettingsController(
        @Autowired private val client: DataApiClient
) {
    @GetMapping("/view")
    fun getUser(
            @AuthenticationPrincipal user: UserPrincipal
    ): Mono<ViewSettingsDTO> {
        return Mono.just(user.user).map { it.toViewSettingsDTO() }
    }

    @PostMapping("/edit")
    fun updateUser(
            @RequestBody userDto: UpdateUserDTO,
            @AuthenticationPrincipal user: UserPrincipal
    ) : Mono<ViewSettingsDTO> {
        return client.putUser(user.id, userDto).map { u ->
            u.toViewSettingsDTO()
        }.onErrorMap(DataApiBadRequestException::class) { e ->
            HttpBadRequestException("Sorry can't do, please send a valid user data change!", e, e.invalidParams)
        }
    }
}