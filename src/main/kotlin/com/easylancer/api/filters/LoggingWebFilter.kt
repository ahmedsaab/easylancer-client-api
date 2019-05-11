package com.easylancer.api.filters

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.apache.logging.log4j.core.config.Order
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

@Component
@Order(2)
class LoggingWebFilter : WebFilter {
    val logger: Logger = LoggerFactory.getLogger("simple-logger")

    // TODO: create a data class for the log object
    fun createLogJson(exchange: ServerWebExchange) : JsonNode {
        val log = jacksonObjectMapper().createObjectNode();
        val request = jacksonObjectMapper().createObjectNode();
        val response = jacksonObjectMapper().createObjectNode();

        val executionTime = exchange.attributes["execution-time"] as Long?
        val exception = exchange.attributes["exception"] as Throwable?
        val requestBody = exchange.attributes["request-body-json"] as JsonNode?
        val responseBody = exchange.attributes["response-body-json"] as JsonNode?

        val statusCode = exchange.response.statusCode?.value()
        val queryParams = jacksonObjectMapper().valueToTree<JsonNode>(exchange.request.queryParams)
        val url = exchange.request.uri.toString()

        request.put("url", url)
        request.set("queryParams", queryParams)
        request.set("body", requestBody)

        response.put("statusCode", statusCode)
        response.set("body", responseBody)

        log.put("prefix", exchange.logPrefix)
        log.put("time", executionTime)
        log.set("request", request)
        log.set("response", response)
        if (exception != null) {
            log.put("exception", exception.javaClass.simpleName)
        }

        return log
    }

    // TODO: create a data class for the log object
    fun createDevLogJson(exchange: ServerWebExchange) : JsonNode {
        val log = jacksonObjectMapper().createObjectNode();

        val executionTime = exchange.attributes["execution-time"] as Long?
        val exception = exchange.attributes["exception"] as Throwable?
        val responseBody = exchange.attributes["response-body-json"] as JsonNode?

        val statusCode = exchange.response.statusCode?.value()
        val url = exchange.request.uri.toString()

        log.put("statusCode", statusCode)
        log.put("url", url)
        log.set("body", responseBody)
        log.put("time", executionTime)

        if (exception != null) {
            exception.printStackTrace()
            val ex = jacksonObjectMapper().createObjectNode();

            ex.put("className", exception.javaClass.simpleName)
            ex.put("message", exception.message)

            log.set("exception", ex)
        }

        return log
    }

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        return chain.filter(exchange).doFinally {
            logger.info(createDevLogJson(exchange).toString())
        }
    }
}

