package com.example.debtmanager.common.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.time.Instant

@RestControllerAdvice
class GlobalExceptionHandler {

    data class ErrorResponse(
        val timestamp: Instant = Instant.now(),
        val status: Int,
        val error: String,
        val message: String
    )

    @ExceptionHandler(ApiException::class)
    fun handleApiException(ex: ApiException): ResponseEntity<ErrorResponse> {
        val body = ErrorResponse(status = ex.status.value(), error = ex.status.reasonPhrase, message = ex.message)
        return ResponseEntity.status(ex.status).body(body)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidation(ex: MethodArgumentNotValidException): ResponseEntity<ErrorResponse> {
        val firstError = ex.bindingResult.allErrors.firstOrNull()
        val message = when (firstError) {
            is FieldError -> "${firstError.field}: ${firstError.defaultMessage}"
            else -> firstError?.defaultMessage ?: "Validation error"
        }
        val body = ErrorResponse(status = HttpStatus.BAD_REQUEST.value(), error = "ValidationError", message = message)
        return ResponseEntity.badRequest().body(body)
    }

    @ExceptionHandler(Exception::class)
    fun handleGeneral(ex: Exception): ResponseEntity<ErrorResponse> {
        val body = ErrorResponse(status = HttpStatus.INTERNAL_SERVER_ERROR.value(), error = "InternalServerError", message = ex.message ?: "Unexpected error")
        return ResponseEntity.internalServerError().body(body)
    }
}