package com.easylancer.api.config


import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.*
import org.springframework.http.HttpHeaders
import org.springframework.web.client.RestTemplate
import org.springframework.web.reactive.function.client.WebClient

import com.easylancer.api.data.EventEmitter
import com.easylancer.api.data.DataApiClient
import com.easylancer.api.exceptions.ErrorResponseDTOComposer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.reactive.config.EnableWebFlux

@Configuration
@EnableWebFlux
class GeneralConfig(@Autowired private val config: DataApiConfig) {
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
    fun dataClient(webClient: WebClient): DataApiClient {
        return DataApiClient(webClient)
    }
    @Bean
    fun eventEmitter(dataApiClient: DataApiClient): EventEmitter {
        return EventEmitter(dataApiClient)
    }
    @Bean
    fun errorResponseComposer(): ErrorResponseDTOComposer {
        return ErrorResponseDTOComposer()
    }
}