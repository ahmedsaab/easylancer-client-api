package com.easylancer.api.data.exceptions

import com.easylancer.api.data.http.DataRequest
import com.easylancer.api.data.http.DataErrorResponse

class DataApiConflictException(
        message: String,
        request: DataRequest,
        override val response: DataErrorResponse,
        cause: Exception? = null
): DataApiException(message, request, response, cause) {
    constructor(message: String, e: DataApiResponseException): this(message, e.request, e.response, e)
}