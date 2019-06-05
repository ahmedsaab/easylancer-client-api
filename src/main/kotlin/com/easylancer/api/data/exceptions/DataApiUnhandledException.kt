package com.easylancer.api.data.exceptions

import com.easylancer.api.data.http.DataRequest
import com.easylancer.api.data.http.DataResponse

class DataApiUnhandledException(
        message: String,
        request: DataRequest,
        response: DataResponse? = null,
        cause: Throwable
): DataApiException(message, request, response, cause)
