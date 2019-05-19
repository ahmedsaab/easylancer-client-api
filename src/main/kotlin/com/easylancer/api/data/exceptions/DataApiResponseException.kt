package com.easylancer.api.data.exceptions

import com.easylancer.api.data.http.DataResponseError
import com.easylancer.api.data.http.DataRequest
import org.springframework.web.client.RestClientResponseException

class DataApiResponseException(
        message: String,
        override val dataRequest: DataRequest,
        val dataResponseError: DataResponseError,
        error: RestClientResponseException? = null
): DataApiException(message, dataRequest, dataResponseError, error)