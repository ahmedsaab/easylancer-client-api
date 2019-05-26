package com.easylancer.api.data.blocking.exceptions

import com.easylancer.api.data.http.DataRequest
import com.easylancer.api.data.http.DataResponse

class DataApiNotFoundException(message: String, request: DataRequest, response: DataResponse, cause: Exception? = null):
        DataApiException(message, request, response, cause) {
    constructor(message: String, e: DataApiResponseException): this(message, e.request, e.response, e)
}