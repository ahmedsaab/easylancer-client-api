package com.easylancer.api.exceptions.http

import org.springframework.http.HttpStatus

class HttpConflictException(message: String, cause: Exception? = null): HttpException(HttpStatus.CONFLICT, message, cause)