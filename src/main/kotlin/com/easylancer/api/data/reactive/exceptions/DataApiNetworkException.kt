package com.easylancer.api.data.reactive.exceptions

import com.easylancer.api.data.http.DataRequest
import org.springframework.web.client.RestClientException
import java.net.ConnectException

class DataApiNetworkException(
        message: String,
        request: DataRequest,
        cause: ConnectException
): DataApiException(message, request, null, cause)
