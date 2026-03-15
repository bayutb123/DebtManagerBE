package com.example.debtmanager.common.exception

import org.springframework.http.HttpStatus

open class ApiException(val status: HttpStatus, override val message: String) : RuntimeException(message)
class NotFoundException(message: String) : ApiException(HttpStatus.NOT_FOUND, message)
class UnauthorizedException(message: String = "Unauthorized") : ApiException(HttpStatus.UNAUTHORIZED, message)
class ValidationException(message: String) : ApiException(HttpStatus.BAD_REQUEST, message)