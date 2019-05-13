package com.easylancer.api.config


import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.*
import org.springframework.http.HttpHeaders
import org.springframework.web.client.RestTemplate
import org.springframework.web.reactive.function.client.WebClient

import com.easylancer.api.data.RestClient
import com.easylancer.api.data.EventEmitter
import com.easylancer.api.exceptions.ErrorResponseDTOComposer
import org.springframework.beans.factory.annotation.Autowired


@Configuration
class Config(@Autowired private val config: DataApi) {
    val apiUrl = "${config.url}:${config.port}"

    @Bean
    fun restTemplate(builder: RestTemplateBuilder): RestTemplate {
        return builder.rootUri(apiUrl).build()
    }
    @Bean
    fun webClient(builder: WebClient.Builder): WebClient {
        return builder.baseUrl(apiUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .defaultHeader(HttpHeaders.USER_AGENT, "Client API")
                .build()
    }
    @Bean
    fun dataApiClient(restTemplate: RestTemplate): RestClient {
        return RestClient(restTemplate)
    }
    @Bean
    fun eventEmitter(restClient: RestClient): EventEmitter {
        return EventEmitter(restClient)
    }
    @Bean
    fun currentUserId(): String {
        return "5cc202a12cc2241945bca94f"
    }
    @Bean
    fun errorResponseComposer(): ErrorResponseDTOComposer {
        return ErrorResponseDTOComposer()
    }
}