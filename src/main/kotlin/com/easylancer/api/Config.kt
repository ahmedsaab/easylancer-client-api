package com.easylancer.api


import com.easylancer.api.controllers.TaskPageController
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.*
import org.springframework.http.HttpHeaders
import org.springframework.web.client.RestTemplate
import org.springframework.web.reactive.function.client.WebClient

import com.easylancer.api.data.DataAPIClient
import com.easylancer.api.data.DataAPIConfig
import com.easylancer.api.data.EventEmitter
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import kotlinx.coroutines.FlowPreview
import org.apache.logging.log4j.core.layout.PatternLayout.KEY
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono
import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.springframework.core.io.InputStreamResource
import org.springframework.core.io.buffer.DataBufferUtils
import reactor.core.publisher.Flux
import java.io.InputStream
import java.io.SequenceInputStream
import java.util.concurrent.Callable


@Configuration
class Config(@Autowired private val config: DataAPIConfig) {
    val logger = LoggerFactory.getLogger("simple-logger")
    val mapper: ObjectMapper = jacksonObjectMapper()

    val apiUrl = "${config.url}:${config.port}"

    @Bean
    fun restTemplate(builder: RestTemplateBuilder): RestTemplate {
        return builder.rootUri(apiUrl).build()
    }
    @Bean
    fun webClient(builder: WebClient.Builder): WebClient {
        return builder.baseUrl(apiUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .defaultHeader(HttpHeaders.USER_AGENT, "Client API")
                .build()
    }
    @Bean
    fun dataApiClient(restTemplate: RestTemplate): DataAPIClient {
        return DataAPIClient(restTemplate)
    }
    @Bean
    fun eventEmitter(dataApiClient: DataAPIClient): EventEmitter {
        return EventEmitter(dataApiClient)
    }
    @Bean
    fun currentUserId(): String {
        return "5cc202a12cc2241945bca94f"
    }
    @Bean
    fun bodyCacheWebFilter(): WebFilter {
        return PayloadLoggingFilter(logger)
    }
//    @Bean
//    fun loggingWebFilter(): WebFilter {
//        return WebFilter { e: ServerWebExchange, c: WebFilterChain ->
//            val startTime = System.currentTimeMillis()
//            val path = e.request.uri.path
////            e.request.body.map {
////                it.asInputStream()
////            }.doOnNext {
////                println(mapper.readValue(it, JsonNode::class.java))
////            }
////            c.filter(e).doAfterTerminate {
////                val executionTime = System.currentTimeMillis() - startTime
////                e.request.body.doOnNext {
////                    println(it)
////                }.subscribe()
////
////                logger.info("Served $path as ${e.response.statusCode} in $executionTime msec")
////            }
////            e.request.body.map {
////                it.asInputStream()
////            }.doOnNext {
////                logger.info(mapper.readValue(it, JsonNode::class.java).toString())
////            }.then(e.response.setComplete())
////            e.request.body.map {
////                it.asInputStream()
////            }.doOnNext {
////                val bodyJson = mapper.readValue(it, JsonNode::class.java);
////
////                logger.info(bodyJson.toString())
////            }.map {
////                DataBufferUtils.read(InputStreamResource(it), e.response.bufferFactory(), 1000)
////            }.then(c.filter(e))
//        }
//    }
}