package com.easylancer.api.data.exceptions

import com.easylancer.api.data.http.DataRequest
import com.easylancer.api.data.http.DataResponse
import org.springframework.web.client.RestClientException

class DataApiUnhandledException(message: String, dataRequest: DataRequest, response: DataResponse? = null, error: RestClientException):
        DataApiException(message, dataRequest, response, error)
