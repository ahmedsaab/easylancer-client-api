package com.easylancer.api.data.blocking.exceptions

import com.easylancer.api.data.http.DataResponseError
import com.easylancer.api.data.http.DataRequest
import org.springframework.web.client.RestClientResponseException

class DataApiResponseException(
        message: String,
        override val request: DataRequest,
        override val response: DataResponseError,
        error: RestClientResponseException? = null
): DataApiException(message, request, response, error)