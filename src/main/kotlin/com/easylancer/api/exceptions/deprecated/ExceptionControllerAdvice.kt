package com.easylancer.api.exceptions.deprecated

import com.easylancer.api.dto.ErrorResponseDTO
import com.easylancer.api.exceptions.ErrorResponseDTOComposer
import org.apache.commons.logging.LogFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.server.ResponseStatusException

/**
 * Handles exceptions thrown from controllers
 */
//@RestControllerAdvice
class ExceptionHandlerControllerAdvice<T : Throwable>(
        @Autowired private val errorResponseDTOComposer: ErrorResponseDTOComposer
) {
    private val log = LogFactory.getLog(ExceptionHandlerControllerAdvice::class.java)

    @RequestMapping(produces = ["application/json"])
    @ExceptionHandler(ResponseStatusException::class)
    fun handleException(ex: ResponseStatusException): ResponseEntity<ErrorResponseDTO> {
        val errorResponse = errorResponseDTOComposer.compose(ex)

        return ResponseEntity(errorResponse, HttpStatus.valueOf(errorResponse.status))
    }
}