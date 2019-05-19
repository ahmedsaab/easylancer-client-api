package com.easylancer.api.data.exceptions

import com.easylancer.api.data.http.ResponseError
import com.easylancer.api.data.http.Request
import org.springframework.web.client.RestClientResponseException

class DataApiResponseException(
        message: String,
        override val request: Request,
        val responseError: ResponseError,
        error: RestClientResponseException? = null
): DataApiException(message, request, responseError, error)