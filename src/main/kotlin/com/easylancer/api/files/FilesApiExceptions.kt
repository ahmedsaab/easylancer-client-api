package com.easylancer.api.files

import org.springframework.core.NestedRuntimeException

abstract class FilesApiException(
        override val message: String,
        cause: Throwable? = null
) : NestedRuntimeException(message, cause) {}

class FilesApiBadRequestException(
        override val message: String,
        cause: Throwable? = null
) : FilesApiException(message, cause) {}

class FilesApiInternalServerException(
        override val message: String,
        cause: Throwable? = null
) : FilesApiException(message, cause) {}

class FilesApiUnexpectedErrorCodeException(
        override val message: String,
        cause: Throwable? = null
) : FilesApiException(message, cause) {}

class FilesApiNetworkException(
        override val message: String,
        cause: Throwable? = null
) : FilesApiException(message, cause) {}

class FilesApiUnhandledException(
        override val message: String,
        cause: Throwable? = null
) : FilesApiException(message, cause) {}