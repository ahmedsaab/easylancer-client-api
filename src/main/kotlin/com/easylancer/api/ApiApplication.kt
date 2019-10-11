package com.easylancer.api

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import javax.annotation.PreDestroy
import javax.annotation.PostConstruct

@SpringBootApplication
class ApiApplication {
    val logger: Logger = LoggerFactory.getLogger("Application")

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
