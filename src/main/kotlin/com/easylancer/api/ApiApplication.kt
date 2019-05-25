package com.easylancer.api

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.reactive.config.EnableWebFlux
import javax.annotation.PreDestroy
import javax.annotation.PostConstruct

@SpringBootApplication
class ApiApplication {
    val logger: Logger = LoggerFactory.getLogger("simple-logger")

    @PostConstruct
    fun startupApplication() {
        logger.info("Started Easylancer Client API ^_^")
    }

    @PreDestroy
    fun shutdownApplication() {
        logger.info("Boom Boom Dead *_*")
    }
}


fun main(args: Array<String>) {
    runApplication<ApiApplication>(*args)
}
