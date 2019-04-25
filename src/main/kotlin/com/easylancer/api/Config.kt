package com.easylancer.api


import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.*
import org.springframework.http.HttpHeaders
import org.springframework.web.client.RestTemplate
import org.springframework.web.reactive.function.client.WebClient

import com.easylancer.api.data.DataAPIClient
import com.easylancer.api.data.EventEmitter

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
    fun dataApiClient(restTemplate: RestTemplate): DataAPIClient {
        return DataAPIClient(restTemplate)
    }
    @Bean
    fun eventEmitter(dataApiClient: DataAPIClient): EventEmitter {
        return EventEmitter(dataApiClient)
    }
    @Bean
    fun currentUserId(): String {
        return "5cc2028c2cc2241945bca94d"
    }
}