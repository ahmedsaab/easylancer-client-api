package com.easylancer.api.filters

import org.apache.logging.log4j.core.config.Order
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

@Component
@Order(1)
class ExecutionTimeWebFilter : WebFilter {
    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        exchange.attributes["startTime"] = System.currentTimeMillis()
        return chain.filter(exchange)
    }
}