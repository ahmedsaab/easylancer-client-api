package com.easylancer.api.data.http

import com.easylancer.api.data.dto.inbound.DataResponseSuccessDTO
import org.springframework.http.HttpStatus

class DataSuccessResponse(
        override val body: DataResponseSuccessDTO
): DataResponse(HttpStatus.OK.value(), body)