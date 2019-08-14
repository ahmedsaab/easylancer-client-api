package com.easylancer.api.exceptions

import com.easylancer.api.exceptions.http.HttpException
import com.easylancer.api.filters.decorateWithBodyLoaders
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.core.io.buffer.DefaultDataBufferFactory
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.server.WebExceptionHandler

@Component
@Order(-2)
class WebExceptionHandler(
        @Autowired private val errorResponseDTOComposer: ErrorResponseDTOComposer
) : WebExceptionHandler {
    override fun handle(exchange: ServerWebExchange, ex: Throwable): Mono<Void> {
        if(ex !is HttpException && ex !is ResponseStatusException) {
            ex.printStackTrace()
        }

        val errorResponseDto = errorResponseDTOComposer.compose(ex)
        val dataBuffer = DefaultDataBufferFactory().wrap(
                jacksonObjectMapper().writeValueAsBytes(errorResponseDto)
        )

        exchange.response.headers.contentType = MediaType.APPLICATION_JSON
        exchange.response.statusCode = HttpStatus.resolve(errorResponseDto.status)
        exchange.response.headers.accessControlAllowOrigin="*"
        exchange.attributes["exception"] = ex

        return decorateWithBodyLoaders(exchange).response.writeWith(Mono.just(dataBuffer))
    }
}