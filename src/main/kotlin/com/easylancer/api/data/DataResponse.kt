package com.easylancer.api.data

import com.easylancer.api.data.dto.DataResponseErrorDTO
import com.easylancer.api.data.dto.DataResponseSuccessDTO
import org.springframework.http.HttpStatus

open class DataResponse(open val statusCode: Int, open val bodyDto: Any?, val bodyString: String?)

class DataSuccessResponse(override val bodyDto: DataResponseSuccessDTO): DataResponse(HttpStatus.OK.value(), bodyDto, null)

class DataErrorResponse: DataResponse {
    constructor(statusCode: Int, bodyDto: DataResponseErrorDTO) : super(statusCode, bodyDto, null)
    constructor(statusCode: Int, bodyString: String) : super(statusCode, null, bodyString)
}