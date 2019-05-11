package com.easylancer.api.filters

import org.apache.logging.log4j.core.config.Order
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

@Component
@Order(3)
class ExecutionTimeWebFilter : WebFilter {
    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val startTime = System.currentTimeMillis()

        return chain.filter(exchange).doOnTerminate {
            exchange.attributes["execution-time"] = System.currentTimeMillis() - startTime
        }
    }
}