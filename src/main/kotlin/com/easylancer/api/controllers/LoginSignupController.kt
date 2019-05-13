package com.easylancer.api.controllers

import com.easylancer.api.data.RestClient
import com.easylancer.api.data.EventEmitter
import com.easylancer.api.data.dto.*
import com.easylancer.api.dto.*
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import kotlinx.coroutines.*

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.security.crypto.password.PasswordEncoder


@RequestMapping("/auth")
@RestController
@FlowPreview
class LoginSignupController(
        @Autowired private val eventEmitter: EventEmitter,
        @Autowired private val dataClient: RestClient,
        @Autowired private val passwordEncoder: PasswordEncoder
) {
    private var mapper: ObjectMapper = jacksonObjectMapper();

    @PostMapping("/log-in")
    suspend fun loginUser(@RequestBody loginDto: Any) {

    }

    @PostMapping("/sign-up")
    suspend fun signupUser(@RequestBody userDto: CreateUserDTO) : ViewUserDTO {
        val userBody = mapper.valueToTree<ObjectNode>(userDto)
        userBody.put("password", passwordEncoder.encode(userDto.password))

        val user: UserDTO = dataClient.postUser(userBody)

        return user.toViewUserDTO();
    }
}