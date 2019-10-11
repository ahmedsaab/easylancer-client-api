package com.easylancer.api.filters

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import net.logstash.logback.argument.StructuredArguments.kv
import net.logstash.logback.encoder.org.apache.commons.lang3.exception.ExceptionUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

class LoggingWebFilter : WebFilter {
   private val logger: Logger = LoggerFactory.getLogger("Http")

    // TODO: create a data class for the log object
    private fun createLogJson(exchange: ServerWebExchange) : JsonNode {
        val log = jacksonObjectMapper().createObjectNode()
        val request = jacksonObjectMapper().createObjectNode()
        val response = jacksonObjectMapper().createObjectNode()

        val executionTime = exchange.attributes["execution-time"] as Long?
        val exception = exchange.attributes["exception"] as Throwable?

        val responseBody = exchange.attributes["response-body-json"] as JsonNode?

        val statusCode = exchange.response.statusCode?.value()
        val queryParams = jacksonObjectMapper().valueToTree<JsonNode>(exchange.request.queryParams)
        val url = exchange.request.uri.toString()

        request.put("url", url)
        request.set("queryParams", queryParams)

        try {
            request.set("body", exchange.attributes["request-body-json"] as JsonNode?)
        } catch (e: Exception) {
            request.put("body", exchange.attributes["request-body-json"] as String)
        }

        response.put("statusCode", statusCode)
        response.set("body", responseBody)

        log.put("prefix", exchange.logPrefix)
        log.put("time", executionTime)
        log.set("request", request)
        log.set("response", response)
        if (exception != null) {
            val error = jacksonObjectMapper().createObjectNode()

            error.put("name", exception.javaClass.simpleName)
            error.put("message", exception.message)
            error.put("stacktrace", ExceptionUtils.getStackTrace(exception))
            log.set("exception", error)
        }

        return log
    }

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val startTime = System.currentTimeMillis()

        return chain.filter(decorateWithBodyLoaders(exchange)).doFinally {
            exchange.attributes["execution-time"] = System.currentTimeMillis() - startTime
            logger.info("http request processed", kv("exchange", createLogJson(exchange)))
        }
    }
}

