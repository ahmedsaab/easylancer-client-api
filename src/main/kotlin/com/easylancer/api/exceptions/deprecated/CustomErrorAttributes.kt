package com.easylancer.api.exceptions.deprecated

import org.slf4j.LoggerFactory
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes
import org.springframework.stereotype.Component

//@Component
class CustomErrorAttribute: DefaultErrorAttributes() {
    private val logger = LoggerFactory.getLogger("simple-logger")

    override fun getErrorAttributes(request: ServerRequest, includeStackTrace: Boolean): Map<String, Any> {
        val errorAttributes = super.getErrorAttributes(request, includeStackTrace)
        log(errorAttributes, request)
        errorAttributes.remove("trace")
        errorAttributes.remove("message")
        return errorAttributes
    }

    private fun log(errorAttributes: MutableMap<String, Any>, request: ServerRequest) {
        val error: Throwable = getError(request)
        val exchange = request.exchange()
        error.printStackTrace()
    }
}