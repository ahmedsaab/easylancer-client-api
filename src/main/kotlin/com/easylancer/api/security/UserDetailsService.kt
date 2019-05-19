package com.easylancer.api.security

import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import com.easylancer.api.data.RestClient
import com.easylancer.api.data.exceptions.DataApiNotFoundException
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.security.core.userdetails.ReactiveUserDetailsPasswordService

@Service
class UserDetailsService(private val client: RestClient) : ReactiveUserDetailsService, ReactiveUserDetailsPasswordService {

    override fun findByUsername(username: String): Mono<UserDetails> {
        return try {
            Mono.just(client.getUserByEmail(username)).map(::User)
        } catch (e: DataApiNotFoundException) {
            Mono.empty<UserDetails>();
        }
    }

    override fun updatePassword(user: UserDetails, newPassword: String): Mono<UserDetails> {
        val userJson = jacksonObjectMapper().createObjectNode();

        userJson.put("password", newPassword)

        return Mono.just(client.putUser(user.username, userJson))
                .then(Mono.just(client.getUserByEmail(user.username)))
                .map(::User)
    }
}
