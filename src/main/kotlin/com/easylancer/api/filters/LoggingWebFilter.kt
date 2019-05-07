package com.easylancer.api.filters

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.reactivestreams.Publisher
import org.slf4j.Logger
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.http.server.reactive.ServerHttpRequestDecorator
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.http.server.reactive.ServerHttpResponseDecorator
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.ServerWebExchangeDecorator
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.publisher.toMono

import java.io.ByteArrayOutputStream
import java.nio.channels.Channels

class LoggingWebFilter(private val logger: Logger) : WebFilter {
    private val om = ObjectMapper()

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        return chain.filter(decorate(exchange))
    }

    // TODO: create a data class for the log object
    private fun createLogJson(
            req: ServerHttpRequest, requestBody: JsonNode?,
            resp: ServerHttpResponse, respBody: JsonNode?, time: Long
    ) : JsonNode {
        val log = om.createObjectNode();
        val request = om.createObjectNode();
        val response = om.createObjectNode();

        request.put("url", req.uri.toString())
        request.set("queryParams", om.valueToTree<JsonNode>(req.queryParams))
        request.set("bodyJson", requestBody)

        response.put("statusCode", resp.statusCode?.value())
        response.set("bodyJson", respBody)

        log.put("time", time)
        log.set("request", request)
        log.set("response", response)

        return log
    }

    private fun decorate(exchange: ServerWebExchange): ServerWebExchange {
        var responseBody: JsonNode? = null
        var requestBody: JsonNode? = null
        val startTime = System.currentTimeMillis()

        val decoratedRequest = object : ServerHttpRequestDecorator(exchange.request) {
            val baos = ByteArrayOutputStream()

            override fun getBody(): Flux<DataBuffer> {
                return super.getBody().map {
                    Channels.newChannel(baos).write(it.asByteBuffer().asReadOnlyBuffer())
                    it
                }.doOnComplete {
                    requestBody = om.readTree(baos.toByteArray())
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
                    responseBody = om.readTree(baos.toByteArray())
                }.doOnTerminate {
                    val executionTime = System.currentTimeMillis() - startTime
                    val logJson = createLogJson(
                            exchange.request, requestBody, exchange.response, responseBody, executionTime
                    )
                    logger.info(logJson.toString())
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
}