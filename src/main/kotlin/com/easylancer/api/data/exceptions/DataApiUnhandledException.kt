package com.easylancer.api.data.exceptions

import com.easylancer.api.data.DataRequest
import com.easylancer.api.data.DataResponse
import com.fasterxml.jackson.databind.JsonNode
import org.springframework.web.client.RestClientException
import org.springframework.web.client.RestClientResponseException

class DataApiUnhandledException(message: String, request: DataRequest, response: DataResponse? = null, error: RestClientException):
        DataApiException(message, request, response, error)
