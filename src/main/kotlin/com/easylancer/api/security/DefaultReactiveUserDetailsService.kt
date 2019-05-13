package com.easylancer.api.security

import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import com.easylancer.api.data.RestClient
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.security.core.userdetails.ReactiveUserDetailsPasswordService

@Service
class DefaultReactiveUserDetailsService(private val client: RestClient) : ReactiveUserDetailsService, ReactiveUserDetailsPasswordService {

    override fun findByUsername(username: String): Mono<UserDetails> {
        return Mono.just(client.getUserByEmail(username)).map(::User)
    }

    override fun updatePassword(user: UserDetails, newPassword: String): Mono<UserDetails> {
        val userJson = jacksonObjectMapper().createObjectNode();

        userJson.put("password", newPassword)

        return Mono.just(client.putUser(user.username, userJson))
                .then(Mono.just(client.getUserByEmail(user.username)))
                .map(::User)
    }
}
