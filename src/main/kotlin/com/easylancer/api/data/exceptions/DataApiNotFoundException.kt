package com.easylancer.api.data.exceptions

import com.easylancer.api.data.http.Request
import com.easylancer.api.data.http.DataResponse

class DataApiNotFoundException(message: String, request: Request, response: DataResponse, cause: Exception? = null):
        DataApiException(message, request, response, cause) {
    constructor(message: String, e: DataApiResponseException): this(message, e.request, e.responseError, e)
}