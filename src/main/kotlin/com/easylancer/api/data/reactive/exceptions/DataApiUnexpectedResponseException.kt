package com.easylancer.api.data.reactive.exceptions

import com.easylancer.api.data.http.DataRequest
import com.easylancer.api.data.http.DataResponse

class DataApiUnexpectedResponseException(
        message: String,
        request: DataRequest,
        response: DataResponse? = null,
        cause: Throwable? = null
): DataApiException(message, request, response, cause)
