package com.easylancer.api

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.Logger
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.http.server.reactive.ServerHttpRequestDecorator
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.ServerWebExchangeDecorator
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

import java.io.ByteArrayOutputStream
import java.io.IOException
import java.nio.channels.Channels

class PayloadLoggingFilter(private val logger: Logger) : WebFilter {
    private val om = ObjectMapper()

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        return if (logger.isInfoEnabled) {
            chain.filter(decorate(exchange))
        } else {
            chain.filter(exchange)
        }
    }

    private fun decorate(exchange: ServerWebExchange): ServerWebExchange {
        val decorated = object : ServerHttpRequestDecorator(exchange.request) {
            val startTime = System.currentTimeMillis()

            override fun getBody(): Flux<DataBuffer> {
                val baos = ByteArrayOutputStream()

                return super.getBody().map { dataBuffer ->
                    try {
                        Channels.newChannel(baos).write(dataBuffer.asByteBuffer().asReadOnlyBuffer())
                    } catch (e: IOException) {
                        logger.error("Unable to read request body due to an error", e)
                    }
                    dataBuffer
                }.doAfterTerminate {
                    val executionTime = System.currentTimeMillis() - startTime
                    val jsonBody = om.readTree(baos.toByteArray())
                    val respStatus = exchange.response.statusCode

                    logger.info("Served $path, body = $jsonBody as $respStatus in $executionTime msec")
                }
            }
        }

        return object : ServerWebExchangeDecorator(exchange) {
            override fun getRequest(): ServerHttpRequest {
                return decorated
            }
        }
    }
}