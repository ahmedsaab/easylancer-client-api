package com.easylancer.api.security

import com.easylancer.api.data.DataApiClient
import org.springframework.core.convert.converter.Converter
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.oauth2.jwt.Jwt
import reactor.core.publisher.Mono

class AuthenticationConverter(val client: DataApiClient): Converter<Jwt, Mono<AbstractAuthenticationToken>> {
    // TODO: handle client errors
    override fun convert(jwt: Jwt): Mono<AbstractAuthenticationToken>? {
        return client.getUserByAuth(jwt.subject).map { dto ->
            UserAuthToken(dto, jwt)
        }
    }
}
