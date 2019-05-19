package com.easylancer.api.data.exceptions

import com.easylancer.api.data.http.DataRequest
import com.easylancer.api.data.http.DataResponse

class DataApiNotFoundException(message: String, dataRequest: DataRequest, response: DataResponse, cause: Exception? = null):
        DataApiException(message, dataRequest, response, cause) {
    constructor(message: String, e: DataApiResponseException): this(message, e.dataRequest, e.dataResponseError, e)
}