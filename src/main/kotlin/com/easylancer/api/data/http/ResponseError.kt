package com.easylancer.api.data.http

import com.easylancer.api.data.dto.DataResponseErrorDTO

class ResponseError: DataResponse {
    constructor(statusCode: Int, body: DataResponseErrorDTO) : super(statusCode, body)
    constructor(statusCode: Int, body: String) : super(statusCode, body)
}