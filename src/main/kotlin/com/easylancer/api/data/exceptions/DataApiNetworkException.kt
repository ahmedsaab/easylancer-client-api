package com.easylancer.api.data.exceptions

import com.easylancer.api.data.http.DataRequest
import java.net.ConnectException

class DataApiNetworkException(
        message: String,
        request: DataRequest,
        cause: ConnectException
): DataApiException(message, request, null, cause)
