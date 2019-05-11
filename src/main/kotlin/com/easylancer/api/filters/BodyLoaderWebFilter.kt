package com.easylancer.api.filters

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.apache.logging.log4j.core.config.Order
import org.reactivestreams.Publisher
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.http.server.reactive.ServerHttpRequestDecorator
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.http.server.reactive.ServerHttpResponseDecorator
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.ServerWebExchangeDecorator
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.publisher.toMono

import java.io.ByteArrayOutputStream
import java.nio.channels.Channels


fun decorateWithBodyLoaders(exchange: ServerWebExchange): ServerWebExchange {
    val decoratedRequest = object : ServerHttpRequestDecorator(exchange.request) {
        val byteStream = ByteArrayOutputStream()

        override fun getBody(): Flux<DataBuffer> {
            return super.getBody().map {
                Channels.newChannel(byteStream).write(it.asByteBuffer().asReadOnlyBuffer())
                it
            }.doOnComplete {
                //TODO: handle JsonParseException from readTree
                exchange.attributes["request-body-json"] = jacksonObjectMapper().readTree(byteStream.toByteArray())
            }
        }
    }

    val decoratedResponse = object : ServerHttpResponseDecorator(exchange.response) {
        val byteStream = ByteArrayOutputStream()

        override fun writeWith(body: Publisher<out DataBuffer>): Mono<Void> {
            return body.toMono().map {
                Channels.newChannel(byteStream).write(it.asByteBuffer().asReadOnlyBuffer())
                it
            }.flatMap {
                super.writeWith(body)
            }.doOnSuccess {
                //TODO: handle JsonParseException from readTree
                exchange.attributes["response-body-json"] = jacksonObjectMapper().readTree(byteStream.toByteArray())
            }
        }
    }

    return object : ServerWebExchangeDecorator(exchange) {
        override fun getRequest(): ServerHttpRequest {
            return decoratedRequest
        }
        override fun getResponse(): ServerHttpResponse {
            return decoratedResponse
        }
    }
}

@Component
@Order(1)
class BodyLoaderWebFilter : WebFilter {
    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        return chain.filter(decorateWithBodyLoaders(exchange))
    }
}