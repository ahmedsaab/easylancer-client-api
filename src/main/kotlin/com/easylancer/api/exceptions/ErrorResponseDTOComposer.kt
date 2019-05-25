package com.easylancer.api.exceptions

import com.easylancer.api.dto.ErrorResponseDTO
import com.easylancer.api.exceptions.http.HttpException
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.exc.MismatchedInputException
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.core.codec.DecodingException
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.server.ServerWebInputException

class ErrorResponseDTOComposer {
    fun compose(ex: Throwable): ErrorResponseDTO {
        return if (ex is HttpException) {
            ErrorResponseDTO(
                    status = ex.status.value(),
                    message = ex.message,
                    data = ex.data
            )
        }  else if (ex is ServerWebInputException && ex.cause is DecodingException &&
                (ex.cause as DecodingException).cause is JsonMappingException
        ) {
            val jsonException = (ex.cause?.cause as JsonMappingException);
            val invalidParams = jsonException.path.map { it.fieldName };

            ErrorResponseDTO(
                    status = 400,
                    message = "The following params are invalid or missing",
                    data = jacksonObjectMapper().valueToTree<ArrayNode>(invalidParams)
            )
        } else if (ex is ResponseStatusException){
            ErrorResponseDTO(
                    status = ex.status.value(),
                    message = ex.message
            )
        } else {
            ErrorResponseDTO(
                    status = 500,
                    message = "Internal Server Error"
            )
        }
    }

    fun composeDev(ex: Throwable): ErrorResponseDTO {
        return ErrorResponseDTO(
                status = 500,
                data = jacksonObjectMapper().valueToTree(ex::class.java.simpleName),
                message = ex.message?: null.toString()
        )
    }
}