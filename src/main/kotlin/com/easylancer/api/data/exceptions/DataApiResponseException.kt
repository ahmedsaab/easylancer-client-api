package com.easylancer.api.data.exceptions

import com.easylancer.api.data.http.DataErrorResponse
import com.easylancer.api.data.http.DataRequest
import org.springframework.web.client.RestClientResponseException

class DataApiResponseException(
        message: String,
        request: DataRequest,
        override val response: DataErrorResponse,
        error: RestClientResponseException? = null
): DataApiException(message, request, response, error)