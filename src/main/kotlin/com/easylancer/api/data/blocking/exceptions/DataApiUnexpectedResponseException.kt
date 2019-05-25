package com.easylancer.api.data.blocking.exceptions

import com.easylancer.api.data.http.DataRequest
import com.easylancer.api.data.http.DataResponse

class DataApiUnexpectedResponseException(message: String, dataRequest: DataRequest, response: DataResponse? = null, error: Exception? = null):
        DataApiException(message, dataRequest, response, error)
