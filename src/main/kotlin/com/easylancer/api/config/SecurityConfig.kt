package com.easylancer.api.config

import com.easylancer.api.data.DataApiClient
import com.easylancer.api.filters.LoggingWebFilter
import com.easylancer.api.security.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.actuate.autoconfigure.security.reactive.EndpointRequest
import org.springframework.boot.autoconfigure.security.reactive.PathRequest
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoders
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.reactive.CorsConfigurationSource

@EnableReactiveMethodSecurity
@EnableWebFluxSecurity
class SecurityConfig(
        @Autowired private val client: DataApiClient
) {
    @Value("\${spring.security.oauth2.resourceserver.jwk.issuer-uri}")
    private val issuerUri: String? = null

    @Bean
    fun springSecurityFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        http
            .cors()
                .configurationSource {
                    CorsConfiguration().applyPermitDefaultValues()
                }
                .and()
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
                    .hasRole(UserRole.ADMIN.name)
                .pathMatchers(HttpMethod.OPTIONS, "/**")
                    .permitAll()
                .pathMatchers("/search/**")
                    .permitAll()
                .pathMatchers("/auth/**")
                    .permitAll()
                .pathMatchers("/profiles/**")
                    .hasRole(UserRole.USER.name)
                .pathMatchers("/tasks/**")
                    .hasRole(UserRole.USER.name)
                .pathMatchers("/users/**")
                    .hasRole(UserRole.USER.name)
                .pathMatchers("/admin/**")
                    .hasRole(UserRole.ADMIN.name)
            .anyExchange()
                .authenticated()
            .and()
            .oauth2ResourceServer()
                .jwt()
                    .jwtDecoder(ReactiveJwtDecoders.fromOidcIssuerLocation(issuerUri))
                    .jwtAuthenticationConverter(AuthenticationConverter(client))

        return http.build()
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder()
    }
}