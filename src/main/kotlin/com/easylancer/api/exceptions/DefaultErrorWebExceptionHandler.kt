package com.easylancer.api.exceptions

import com.easylancer.api.filters.decorateWithBodyLoaders
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler
import org.springframework.stereotype.Component
import org.springframework.core.io.buffer.DefaultDataBufferFactory
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType

@Component
class DefaultErrorWebExceptionHandler(
        @Autowired private val errorResponseDTOComposer: ErrorResponseDTOComposer
) : ErrorWebExceptionHandler {
    override fun handle(exchange: ServerWebExchange, ex: Throwable): Mono<Void> {
        val errorResponseDto = errorResponseDTOComposer.compose(ex);
        val dataBuffer = DefaultDataBufferFactory().wrap(
                jacksonObjectMapper().writeValueAsBytes(errorResponseDto)
        )

        exchange.response.headers.contentType = MediaType.APPLICATION_JSON;
        exchange.response.statusCode = HttpStatus.resolve(errorResponseDto.status);
        exchange.attributes["exception"] = ex;

        return decorateWithBodyLoaders(exchange).response.writeWith(Mono.just(dataBuffer))
    }
}