package br.com.ebury.exceptions

import br.com.ebury.dto.ErrorResponseDto
import br.com.ebury.exceptions.httpstatus.BadRequestException
import br.com.ebury.exceptions.httpstatus.ForbiddenException
import br.com.ebury.exceptions.httpstatus.NotFoundException
import br.com.ebury.exceptions.httpstatus.UnauthorizedException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {
    private val logger: Logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @ExceptionHandler(NotFoundException::class)
    fun handleNotFoundException(ex: NotFoundException): ResponseEntity<ErrorResponseDto> {
        logger.error("NotFoundException: ${ex.message}", ex)
        val errorResponse =
            ErrorResponseDto(
                message = ex.message ?: "Resource not found",
                status = HttpStatus.NOT_FOUND.value(),
            )
        return ResponseEntity(errorResponse, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(BadRequestException::class)
    fun handleBadRequestException(ex: BadRequestException): ResponseEntity<ErrorResponseDto> {
        logger.error("BadRequestException: ${ex.message}", ex)
        val errorResponse =
            ErrorResponseDto(
                message = ex.message ?: "Invalid request",
                status = HttpStatus.BAD_REQUEST.value(),
            )
        return ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(ex: IllegalArgumentException): ResponseEntity<ErrorResponseDto> {
        logger.error("IllegalArgumentException: ${ex.message}", ex)
        val errorResponse =
            ErrorResponseDto(
                message = ex.message ?: "Invalid request",
                status = HttpStatus.BAD_REQUEST.value(),
            )
        return ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(UnauthorizedException::class)
    fun handleUnauthorizedException(ex: UnauthorizedException): ResponseEntity<ErrorResponseDto> {
        logger.error("UnauthorizedException: ${ex.message}", ex)
        val errorResponse =
            ErrorResponseDto(
                message = ex.message ?: "Unauthorized access",
                status = HttpStatus.UNAUTHORIZED.value(),
            )
        return ResponseEntity(errorResponse, HttpStatus.UNAUTHORIZED)
    }

    @ExceptionHandler(ForbiddenException::class)
    fun handleForbiddenException(ex: ForbiddenException): ResponseEntity<ErrorResponseDto> {
        logger.error("ForbiddenException: ${ex.message}", ex)
        val errorResponse =
            ErrorResponseDto(
                message = ex.message ?: "Access forbidden",
                status = HttpStatus.FORBIDDEN.value(),
            )
        return ResponseEntity(errorResponse, HttpStatus.FORBIDDEN)
    }

    @ExceptionHandler(Exception::class)
    fun handleGenericException(ex: Exception): ResponseEntity<ErrorResponseDto> {
        logger.error("GenericException: ${ex.message}", ex)
        val errorResponse =
            ErrorResponseDto(
                message = "An unexpected error occurred. Please try again later.",
                status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
            )
        return ResponseEntity(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR)
    }
}
