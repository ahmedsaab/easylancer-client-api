package com.easylancer.api


import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.*
import org.springframework.http.HttpHeaders
import org.springframework.web.client.RestTemplate
import org.springframework.web.reactive.function.client.WebClient

import com.easylancer.api.data.DataAPIClient
import com.easylancer.api.data.DataAPIConfig
import com.easylancer.api.data.EventEmitter
import com.easylancer.api.exceptions.ErrorResponseDTOComposer
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.slf4j.Logger


@Configuration
class Config(@Autowired private val config: DataAPIConfig) {
    val logger: Logger = LoggerFactory.getLogger("simple-logger")
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
    fun dataApiClient(restTemplate: RestTemplate): DataAPIClient {
        return DataAPIClient(restTemplate)
    }
    @Bean
    fun eventEmitter(dataApiClient: DataAPIClient): EventEmitter {
        return EventEmitter(dataApiClient)
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