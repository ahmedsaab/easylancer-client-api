package com.easylancer.api.data.exceptions

import com.easylancer.api.data.DataRequest
import com.easylancer.api.data.DataResponse
import org.springframework.web.client.RestClientException
import org.springframework.web.client.RestClientResponseException

class DataApiUnexpectedResponseException(message: String, request: DataRequest, response: DataResponse? = null, error: Exception? = null):
        DataApiException(message, request, response, error)
