package com.easylancer.api.files

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import reactor.core.publisher.Mono
import java.net.ConnectException


class FilesApiClient(
        @Autowired private val webClient: WebClient
) {
    private val mapper: ObjectMapper = jacksonObjectMapper()

    private fun transformException(e: Throwable): FilesApiException {
        return when (e) {
            is WebClientResponseException ->
                when (e.statusCode.value()) {
                    400 -> FilesApiBadRequestException("Invalid or missing params", e)
                    500 -> FilesApiInternalServerException("Internal server error", e)
                    else -> FilesApiUnexpectedErrorCodeException("Unexpected error code received", e)
                }
            is ConnectException ->
                FilesApiNetworkException("Failed to access server", e)
            else ->
                FilesApiUnhandledException("Unhandled exception occurred", e)
        }
    }

    private fun post(url: String, json: ObjectNode): Mono<Unit> {
        return webClient.post()
                .uri(url)
                .body(BodyInserters.fromObject(json))
                .exchange()
                .onErrorMap { e ->
                    transformException(e)
                }.map {  }
    }


    fun check(urls: List<String>): Mono<Unit> {
        val urlsArray = mapper.createArrayNode();
        urls.forEach { urlsArray.add(it) }
        val json = mapper.createObjectNode();
        json.set("urls", urlsArray)

        return post("/update-files", json)
    }

    fun confirm(urls: List<String>): Mono<Unit> {
        val urlsArray = mapper.createArrayNode();
        urls.forEach { urlsArray.add(it) }
        val json = mapper.createObjectNode();
        json.set("urls", urlsArray)
        json.put("confirm", true)

        return post("/update-files", json)
    }

    fun remove(urls: List<String>): Mono<Unit> {
        val urlsArray = mapper.createArrayNode();
        urls.forEach { urlsArray.add(it) }
        val json = mapper.createObjectNode();
        json.set("urls", urlsArray)
        json.put("confirm", false)

        return post("/update-files", json)
    }
}