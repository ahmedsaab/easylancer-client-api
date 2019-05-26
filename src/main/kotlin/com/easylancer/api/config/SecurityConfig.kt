package com.easylancer.api.config

import com.easylancer.api.filters.LoggingWebFilter
import com.easylancer.api.security.Role
import com.easylancer.api.security.UserDetailsService
import com.easylancer.api.security.UserRolesJwtAuthenticationConverter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.actuate.autoconfigure.security.reactive.EndpointRequest
import org.springframework.boot.autoconfigure.security.reactive.PathRequest
import org.springframework.context.annotation.Bean
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoders
import org.springframework.security.web.server.SecurityWebFilterChain

@EnableReactiveMethodSecurity
@EnableWebFluxSecurity
class SecurityConfig(
        @Autowired private val userDetailsService: UserDetailsService
) {
    @Value("\${spring.security.oauth2.resourceserver.jwk.issuer-uri}")
    private val issuerUri: String? = null

    @Bean
    fun springSecurityFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        http
            .csrf()
                .disable()
            .addFilterAt(LoggingWebFilter(), SecurityWebFiltersOrder.FIRST)
            .authorizeExchange()
                .matchers(PathRequest.toStaticResources().atCommonLocations())
                    .permitAll()
                .matchers(EndpointRequest.to("health"))
                    .permitAll()
                .matchers(EndpointRequest.to("info"))
                    .permitAll()
                .matchers(EndpointRequest.toAnyEndpoint())
                    .hasRole(Role.ADMIN.name)
                .pathMatchers("/search/**")
                    .permitAll()
                .pathMatchers("/auth/**")
                    .permitAll()
                .pathMatchers("/profiles/**")
                    .authenticated()
                .pathMatchers("/tasks/**")
                    .authenticated()
                .pathMatchers("/users/**")
                    .hasRole(Role.USER.name)
                .pathMatchers("/admin/**")
                    .hasRole(Role.ADMIN.name)
            .anyExchange()
                .authenticated()
            .and()
                .oauth2ResourceServer()
                .jwt()
                .jwtAuthenticationConverter(userJwtAuthenticationConverter())

        return http.build()
    }

    @Bean
    fun jwtDecoder(): ReactiveJwtDecoder {
        return ReactiveJwtDecoders.fromOidcIssuerLocation(issuerUri)
    }

    @Bean
    fun userJwtAuthenticationConverter(): UserRolesJwtAuthenticationConverter {
        return UserRolesJwtAuthenticationConverter(userDetailsService)
    }

    @Bean
    fun userRolesJwtAuthenticationConverter(): UserRolesJwtAuthenticationConverter {
        return UserRolesJwtAuthenticationConverter(userDetailsService)
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder()
    }
}