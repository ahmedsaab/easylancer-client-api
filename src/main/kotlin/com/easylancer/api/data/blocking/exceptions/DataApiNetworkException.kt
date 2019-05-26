package com.easylancer.api.data.blocking.exceptions

import com.easylancer.api.data.http.DataRequest
import org.springframework.web.client.RestClientException

class DataApiNetworkException(message: String, request: DataRequest, error: RestClientException):
        DataApiException(message, request, null, error)
