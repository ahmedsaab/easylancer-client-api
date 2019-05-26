package com.easylancer.api.data.blocking.exceptions

import com.easylancer.api.data.http.DataRequest
import com.easylancer.api.data.http.DataResponse
import org.springframework.web.client.RestClientException

class DataApiUnhandledException(message: String, request: DataRequest, response: DataResponse? = null, error: RestClientException):
        DataApiException(message, request, response, error)
