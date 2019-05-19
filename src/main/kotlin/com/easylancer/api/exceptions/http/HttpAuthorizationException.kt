package com.easylancer.api.exceptions.http

import org.springframework.http.HttpStatus

class HttpAuthorizationException(message: String): HttpException(HttpStatus.UNAUTHORIZED, message)