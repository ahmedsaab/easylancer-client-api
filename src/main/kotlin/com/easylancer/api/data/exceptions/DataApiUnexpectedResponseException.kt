package com.easylancer.api.data.exceptions

import com.easylancer.api.data.Request
import com.easylancer.api.data.DataResponse

class DataApiUnexpectedResponseException(message: String, request: Request, response: DataResponse? = null, error: Exception? = null):
        DataApiException(message, request, response, error)
