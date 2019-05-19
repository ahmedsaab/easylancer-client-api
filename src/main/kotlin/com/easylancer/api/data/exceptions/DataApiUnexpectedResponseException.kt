package com.easylancer.api.data.exceptions

import com.easylancer.api.data.http.Request
import com.easylancer.api.data.http.DataResponse

class DataApiUnexpectedResponseException(message: String, request: Request, response: DataResponse? = null, error: Exception? = null):
        DataApiException(message, request, response, error)
