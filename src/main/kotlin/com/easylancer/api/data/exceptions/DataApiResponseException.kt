package com.easylancer.api.data.exceptions

import com.easylancer.api.data.DataRequest
import com.easylancer.api.data.DataResponse
import org.springframework.web.client.RestClientResponseException

class DataApiResponseException(message: String, request: DataRequest, override val response: DataResponse, error: RestClientResponseException? = null):
        DataApiException(message, request, response, error)