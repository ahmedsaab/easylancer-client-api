package com.easylancer.api.data.http

import com.easylancer.api.data.dto.DataResponseSuccessDTO
import org.springframework.http.HttpStatus

class DataResponseSuccess(
        override val body: DataResponseSuccessDTO
): DataResponse(HttpStatus.OK.value(), body)