package com.easylancer.api.data.exceptions

import com.easylancer.api.data.Request
import com.easylancer.api.data.DataResponse
import org.springframework.web.client.RestClientResponseException

class DataApiResponseException(message: String, request: Request, override val response: DataResponse, error: RestClientResponseException? = null):
        DataApiException(message, request, response, error)