package com.easylancer.api.exceptions

import com.easylancer.api.dto.ErrorResponseDTO
import com.easylancer.api.exceptions.http.HttpException

class ErrorResponseDTOComposer {
    fun compose(ex: Throwable): ErrorResponseDTO {
        if (ex is HttpException) {
            return ErrorResponseDTO(status = ex.status.value(), message = ex.message)
        } else {
            return ErrorResponseDTO(status = 500, message = "Internal Server Error")
        }
    }
}