package com.easylancer.api

import org.springframework.http.HttpStatus
import java.time.LocalDateTime
import com.fasterxml.jackson.annotation.JsonFormat


internal class ApiError private constructor() {

    private var status: HttpStatus? = null
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private val timestamp: LocalDateTime = LocalDateTime.now()
    private var message: String? = null
    private var debugMessage: String? = null
    private val subErrors: List<ApiSubError>? = null

    constructor(status: HttpStatus) : this() {
        this.status = status
    }

    constructor(status: HttpStatus, ex: Throwable) : this() {
        this.status = status
        this.message = "Unexpected error"
        this.debugMessage = ex.localizedMessage
    }

    constructor(status: HttpStatus, message: String, ex: Throwable) : this() {
        this.status = status
        this.message = message
        this.debugMessage = ex.localizedMessage
    }
}

internal abstract class ApiSubError


internal data class ApiValidationError(private val `object`: String, private val message: String) : ApiSubError() {
    private val field: String? = null
    private val rejectedValue: Any? = null
}
