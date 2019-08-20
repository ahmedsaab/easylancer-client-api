package com.easylancer.api.config


import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.*
import org.springframework.http.HttpHeaders
import org.springframework.web.client.RestTemplate
import org.springframework.web.reactive.function.client.WebClient

import com.easylancer.api.data.EventEmitter
import com.easylancer.api.data.DataApiClient
import com.easylancer.api.exceptions.ErrorResponseDTOComposer
import com.easylancer.api.files.FilesApiClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.reactive.config.EnableWebFlux

@Configuration
@EnableWebFlux
class GeneralConfig(
        @Autowired private val dataApiConfig: DataApiConfig,
        @Autowired private val filesApiConfig: FilesApiConfig
) {
    val dataApiUrl = "${dataApiConfig.url}:${dataApiConfig.port}"
    val filesApiUrl = filesApiConfig.url

    @Bean
    fun restTemplate(builder: RestTemplateBuilder): RestTemplate {
        return builder.rootUri(dataApiUrl).build()
    }
    @Bean
    fun webClient(builder: WebClient.Builder): WebClient {
        return builder.baseUrl(dataApiUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .defaultHeader(HttpHeaders.USER_AGENT, "Client API")
                .build()
    }
    @Bean
    fun filesWebClient(builder: WebClient.Builder): WebClient {
        return builder.baseUrl(filesApiUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .defaultHeader(HttpHeaders.USER_AGENT, "Client API")
                .build()
    }
    @Bean
    fun dataClient(webClient: WebClient): DataApiClient {
        return DataApiClient(webClient)
    }
    @Bean
    fun filesClient(filesWebClient: WebClient): FilesApiClient {
        return FilesApiClient(filesWebClient)
    }
    @Bean
    fun eventEmitter(dataApiClient: DataApiClient, filesClient: FilesApiClient): EventEmitter {
        return EventEmitter(dataApiClient, filesClient)
    }
    @Bean
    fun errorResponseComposer(): ErrorResponseDTOComposer {
        return ErrorResponseDTOComposer()
    }
}