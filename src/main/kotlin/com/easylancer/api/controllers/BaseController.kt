package com.easylancer.api.controllers

import com.easylancer.api.data.*
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.slf4j.LoggerFactory

abstract class BaseController {
    protected abstract val dataClient: RestClient
    protected abstract val eventEmitter: EventEmitter

    protected val mapper: ObjectMapper = jacksonObjectMapper()
    protected val logger = LoggerFactory.getLogger("simple-logger")
}