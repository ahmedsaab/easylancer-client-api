package com.easylancer.api.data.exceptions

import com.easylancer.api.data.DataRequest
import com.easylancer.api.data.DataResponse
import com.fasterxml.jackson.databind.JsonNode
import org.springframework.web.client.RestClientException
import org.springframework.web.client.RestClientResponseException

class DataApiNetworkException(message: String, request: DataRequest, error: RestClientException):
        DataApiException(message, request, null, error)
