package com.easylancer.api.data.exceptions

import com.easylancer.api.data.DataRequest
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

abstract class DataApiException: RuntimeException {
    protected val mapper: ObjectMapper = jacksonObjectMapper()
    private var request: DataRequest? = null

    abstract val reason: JsonNode?

    constructor(message: String, request: DataRequest, ex: Exception?): super(message, ex) {
        this.request = request
    }
    constructor(message: String, request: DataRequest): super(message) {
        this.request = request
    }
    constructor(ex: Exception, request: DataRequest): super(ex) {
        this.request = request
    }
    fun toLogJson(): JsonNode {
        val log = mapper.createObjectNode();
        log.set("reason", reason)
        log.set("request", mapper.valueToTree(request))
        return log
    }
}