package com.easylancer.api

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import javax.annotation.PreDestroy
import javax.annotation.PostConstruct



@SpringBootApplication
class ApiApplication {
    @PostConstruct
    fun startupApplication() {
        println("Started Application")
    }

    @PreDestroy
    fun shutdownApplication() {
        println("Stopped Application")
    }
}


fun main(args: Array<String>) {
    runApplication<ApiApplication>(*args)
}
