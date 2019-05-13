package com.easylancer.api.data.exceptions

import com.easylancer.api.data.Request
import com.easylancer.api.data.DataResponse
import org.springframework.web.client.RestClientException

class DataApiUnhandledException(message: String, request: Request, response: DataResponse? = null, error: RestClientException):
        DataApiException(message, request, response, error)
