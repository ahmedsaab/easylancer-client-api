package com.easylancer.api.controllers

import com.easylancer.api.data.EventEmitter
import com.easylancer.api.data.DataApiClient
import com.easylancer.api.dto.*
import com.easylancer.api.security.UserPrincipal

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import org.springframework.security.crypto.password.PasswordEncoder
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.security.core.annotation.AuthenticationPrincipal
import reactor.core.publisher.Mono

@RequestMapping("/auth")
@RestController
class AuthController(
        @Autowired private val eventEmitter: EventEmitter,
        @Autowired private val client: DataApiClient,
        @Autowired private val passwordEncoder: PasswordEncoder
) {
    private var mapper: ObjectMapper = jacksonObjectMapper();

    @GetMapping("/login")
    suspend fun loginUser(@RequestParam params: Map<String,String>): Map<String,String> {
        return params
    }

    @PostMapping("/sign-up")
    suspend fun signupUser(@RequestBody userDto: CreateUserDTO) : Unit {

    }
}