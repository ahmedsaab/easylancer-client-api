package com.easylancer.api.data.http

import com.easylancer.api.data.dto.inbound.DataResponseErrorDTO

class DataErrorResponse(
        statusCode: Int,
        override val body: DataResponseErrorDTO
) : DataResponse(statusCode, body)