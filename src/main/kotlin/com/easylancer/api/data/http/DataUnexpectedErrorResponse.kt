package com.easylancer.api.data.http

class DataUnexpectedErrorResponse(
        statusCode: Int,
        override val body: String? = null
) : DataResponse(statusCode, body)