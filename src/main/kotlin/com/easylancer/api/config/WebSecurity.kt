package com.easylancer.api.config

import com.easylancer.api.filters.LoggingWebFilter
import com.easylancer.api.security.Role
import org.springframework.boot.actuate.autoconfigure.security.reactive.EndpointRequest
import org.springframework.boot.autoconfigure.security.reactive.PathRequest
import org.springframework.context.annotation.Bean
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.server.authentication.logout.RedirectServerLogoutSuccessHandler
import org.springframework.security.web.server.authentication.logout.ServerLogoutSuccessHandler
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.authentication.AuthenticationFailureHandler
import org.springframework.security.web.server.SecurityWebFilterChain
import java.net.URI

@EnableReactiveMethodSecurity
@EnableWebFluxSecurity
class WebSecurityConfiguration {
    @Bean
    fun springSecurityFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        return http
                .csrf()
                    .disable()
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
                        .hasRole(Role.USER.name)
                    .pathMatchers("/tasks/**")
                        .hasRole(Role.USER.name)
                    .pathMatchers("/users/**")
                        .hasRole(Role.USER.name)
                    .pathMatchers("/admin/**")
                        .hasRole(Role.ADMIN.name)
                    .anyExchange()
                        .authenticated()
                .and()
                .httpBasic()
                .and()
                .formLogin()
                .and()
                .logout()
                    .logoutSuccessHandler(logoutSuccessHandler())
                .and()
                .addFilterAt(LoggingWebFilter(), SecurityWebFiltersOrder.FIRST)
                .build()
    }

    @Bean
    fun logoutSuccessHandler(): ServerLogoutSuccessHandler {
        val logoutSuccessHandler = RedirectServerLogoutSuccessHandler()
        logoutSuccessHandler.setLogoutSuccessUrl(URI.create("/login"))
        return logoutSuccessHandler
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder()
    }
}
