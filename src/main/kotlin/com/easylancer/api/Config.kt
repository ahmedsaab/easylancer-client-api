package com.easylancer.api


import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.*
import org.springframework.http.HttpHeaders
import org.springframework.web.client.RestTemplate
import org.springframework.web.reactive.function.client.WebClient

import com.easylancer.api.DataAPIClient

@Configuration
class Config {
    @Bean
    fun restTemplate(builder: RestTemplateBuilder): RestTemplate {
        return builder.rootUri("http://localhost:3000").build()
    }
    @Bean
    fun webClient(builder: WebClient.Builder): WebClient {
        return builder.baseUrl("http://localhost:3000")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .defaultHeader(HttpHeaders.USER_AGENT, "Client API")
                .build()
    }
    @Bean
    fun dataApiClient(builder: RestTemplateBuilder): DataAPIClient {
        return DataAPIClient(restTemplate(builder))
    }
}