package com.easylancer.api.data.exceptions

import com.easylancer.api.data.http.Request
import org.springframework.web.client.RestClientException

class DataApiNetworkException(message: String, request: Request, error: RestClientException):
        DataApiException(message, request, null, error)
