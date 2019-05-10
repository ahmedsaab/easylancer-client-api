package com.easylancer.api.filters

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import net.logstash.logback.encoder.org.apache.commons.lang.exception.ExceptionUtils
import org.apache.logging.log4j.core.config.Order
import org.reactivestreams.Publisher
import org.slf4j.Logger
import org.slf4j.LoggerFactory
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

val logger: Logger = LoggerFactory.getLogger("simple-logger")

fun decorate(exchange: ServerWebExchange): ServerWebExchange {
    val decoratedRequest = object : ServerHttpRequestDecorator(exchange.request) {
        val baos = ByteArrayOutputStream()

        override fun getBody(): Flux<DataBuffer> {
            return super.getBody().map {
                Channels.newChannel(baos).write(it.asByteBuffer().asReadOnlyBuffer())
                it
            }.doOnComplete {
                exchange.attributes["requestBodyJson"] = jacksonObjectMapper().readTree(baos.toByteArray())
            }
        }
    }

    val decoratedResponse = object : ServerHttpResponseDecorator(exchange.response) {
        val baos = ByteArrayOutputStream()

        override fun writeWith(body: Publisher<out DataBuffer>): Mono<Void> {
            return body.toMono().map {
                Channels.newChannel(baos).write(it.asByteBuffer().asReadOnlyBuffer())
                it
            }.flatMap {
                super.writeWith(body)
            }.doOnSuccess {
                exchange.attributes["responseBodyJson"] = jacksonObjectMapper().readTree(baos.toByteArray())
            }.doOnTerminate {
                logger.info(createLogJson(exchange).toString())
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

// TODO: create a data class for the log object
fun createLogJson(exchange: ServerWebExchange) : JsonNode {
    val log = jacksonObjectMapper().createObjectNode();
    val request = jacksonObjectMapper().createObjectNode();
    val response = jacksonObjectMapper().createObjectNode();
    val executionTime = System.currentTimeMillis() - exchange.attributes["startTime"] as Long
    val exception: Throwable? = exchange.attributes["exception"] as Throwable?

    request.put("url", exchange.request.uri.toString())
    request.set("queryParams", jacksonObjectMapper().valueToTree<JsonNode>(exchange.request.queryParams))
    request.set("body", exchange.attributes.get("requestBodyJson") as JsonNode?)

    response.put("statusCode", exchange.response.statusCode?.value())
    response.set("body", exchange.attributes.get("responseBodyJson") as JsonNode?)

    log.put("time", executionTime)
    log.set("request", request)
    log.set("response", response)
    if (exception != null) {
//        log.put("exception", ExceptionUtils.getStackTrace(exception))
        exception.printStackTrace()
    }
    return log
}

@Component
@Order(2)
class LoggingWebFilter : WebFilter {
    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        return chain.filter(decorate(exchange))
    }
}