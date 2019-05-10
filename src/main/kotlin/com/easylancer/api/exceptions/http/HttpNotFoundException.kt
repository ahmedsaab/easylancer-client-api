package com.easylancer.api.exceptions.http

import org.springframework.http.HttpStatus

class HttpNotFoundException(message: String): HttpException(HttpStatus.NOT_FOUND, message)