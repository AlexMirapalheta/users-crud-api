package com.kotlinspring.learn.userscrudapi.handler

import com.kotlinspring.learn.userscrudapi.exception.ErrorMessage
import com.kotlinspring.learn.userscrudapi.exception.ExceptionResponse
import com.kotlinspring.learn.userscrudapi.users.exception.UserNotFoundException
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingPathVariableException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.servlet.resource.NoResourceFoundException

@ControllerAdvice
class ExceptionsHandler {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(ExceptionHandler::class.java)
    }
    @ExceptionHandler(Exception::class)
    fun handleAllExceptions (
        ex: Exception, request: WebRequest
    ): ResponseEntity<ExceptionResponse> {
        val exceptionResponse = ExceptionResponse(
            listOf(ErrorMessage(
                code = "Internal Server Error",
                description = "Error caused by internal server trouble: ${ex.message}"
            ))
        )

        logger.error("handleAllExceptions [${HttpStatus.INTERNAL_SERVER_ERROR}] $ex")

        return ResponseEntity<ExceptionResponse>(exceptionResponse, HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @ExceptionHandler(UserNotFoundException::class)
    fun handleUserNotFoundExceptions (
        ex: Exception, request: WebRequest
    ): ResponseEntity<ExceptionResponse> {
        val exceptionResponse = ExceptionResponse(
            listOf(ErrorMessage(
                code = "User Not Found",
                description = "Requested user could not be found"
            ))
        )

        logger.error("handleUserNotFoundExceptions [${HttpStatus.NOT_FOUND}] $ex")

        return ResponseEntity<ExceptionResponse>(exceptionResponse, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun handleValidationException (
        ex: MethodArgumentTypeMismatchException, request: WebRequest
    ): ResponseEntity<ExceptionResponse> {
        val exceptionResponse = ExceptionResponse(
            listOf(ErrorMessage(
                code = "Method Argument Type Mismatch",
                description = "Failed to convert '${ex.propertyName}' property value to required type"
            ))
        )

        logger.error("MethodArgumentTypeMismatchException [${HttpStatus.BAD_REQUEST}] $ex")

        return ResponseEntity<ExceptionResponse>(exceptionResponse, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValidException(
        req: HttpServletRequest, ex: MethodArgumentNotValidException
    ): ResponseEntity<ExceptionResponse> {
        val errorMessages: List<ErrorMessage> = ArrayList()
        for (error in ex.bindingResult.allErrors) {
            errorMessages.addLast(
                ErrorMessage(
                    code = "Invalid Payload",
                    description = error.defaultMessage ?: "Invalid payload attribute"
            ))
        }
        val exceptionResponse = ExceptionResponse(errorMessages)

        logger.error("handleMethodArgumentNotValidException [${HttpStatus.BAD_REQUEST}] $ex")

        return ResponseEntity<ExceptionResponse>(exceptionResponse, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(DataIntegrityViolationException::class)
    fun handleDataIntegrityViolationException(
        ex: DataIntegrityViolationException, request: WebRequest
    ): ResponseEntity<ExceptionResponse> {
        val exceptionResponse = ExceptionResponse(
            listOf(ErrorMessage(
                code = "Data Integrity Violation",
                description = "Exclusive Data Restriction: could not execute statement"
            ))
        )

        logger.error("handleDataIntegrityViolationException [${HttpStatus.BAD_REQUEST}] $ex")

        return ResponseEntity<ExceptionResponse>(exceptionResponse, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleHttpMessageNotReadableException(
        ex: HttpMessageNotReadableException, request: WebRequest
    ): ResponseEntity<ExceptionResponse> {
        val exceptionResponse = ExceptionResponse(
            listOf(ErrorMessage(
                code = "Http Message Not Readable",
                description = "JSON parse error: instantiation of value failed for some property due to missing (therefore NULL) value for creator parameter which is a non-nullable type"
            ))
        )

        logger.error("HttpMessageNotReadableException [${HttpStatus.BAD_REQUEST}] $ex")

        return ResponseEntity<ExceptionResponse>(exceptionResponse, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(MissingPathVariableException::class)
    fun handleMissingPathVariableException(
        ex: MissingPathVariableException, request: WebRequest
    ): ResponseEntity<ExceptionResponse> {
        val exceptionResponse = ExceptionResponse(
            listOf(ErrorMessage(
                code = "Missing Path Variable",
                description = ex.message
            ))
        )

        logger.error("MissingPathVariableException [${HttpStatus.BAD_REQUEST}] $ex")

        return ResponseEntity<ExceptionResponse>(exceptionResponse, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(NoResourceFoundException::class)
    fun handleNoResourceFoundException(
        ex: NoResourceFoundException, request: WebRequest
    ): ResponseEntity<ExceptionResponse> {
        val exceptionResponse = ExceptionResponse(
            listOf(ErrorMessage(
                code = "No Resource Found",
                description = "${ex.message}"
            ))
        )

        logger.error("NoResourceFoundException [${HttpStatus.NOT_FOUND}] $ex")

        return ResponseEntity<ExceptionResponse>(exceptionResponse, HttpStatus.NOT_FOUND)
    }

}