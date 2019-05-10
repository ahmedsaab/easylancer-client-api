package com.easylancer.api.exceptions.http

import org.springframework.core.NestedRuntimeException
import org.springframework.http.HttpStatus

abstract class HttpException(val status: HttpStatus, override val message: String) : NestedRuntimeException(message)