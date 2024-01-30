package com.kotlinspring.learn.userscrudapi.handler

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
import java.time.LocalDateTime

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
            LocalDateTime.now(),
            ex.message,
            request.getDescription(false)
        )

        logger.info("handleAllExceptions [${HttpStatus.INTERNAL_SERVER_ERROR}] $ex")

        return ResponseEntity<ExceptionResponse>(exceptionResponse, HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @ExceptionHandler(UserNotFoundException::class)
    fun handleUserNotFoundExceptions (
        ex: Exception, request: WebRequest
    ): ResponseEntity<ExceptionResponse> {
        val exceptionResponse = ExceptionResponse(
            LocalDateTime.now(),
            ex.message,
            request.getDescription(false)
        )

        logger.info("handleUserNotFoundExceptions [${HttpStatus.NOT_FOUND}] $ex")

        return ResponseEntity<ExceptionResponse>(exceptionResponse, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun handleValidationException (
        ex: MethodArgumentTypeMismatchException, request: WebRequest
    ): ResponseEntity<ExceptionResponse> {
        val exceptionResponse = ExceptionResponse(
            LocalDateTime.now(),
            "Method Argument Type Mismatch",
            ex.message ?: request.getDescription(false)
        )

        logger.info("MethodArgumentTypeMismatchException [${HttpStatus.BAD_REQUEST}] $ex")

        return ResponseEntity<ExceptionResponse>(exceptionResponse, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValidException(
        req: HttpServletRequest, ex: MethodArgumentNotValidException
    ): ResponseEntity<ExceptionResponse> {
        val exceptionResponse = ExceptionResponse(
                LocalDateTime.now(),
                "Invalid Payload",
                (ex.bindingResult.allErrors.map { it.defaultMessage ?: "" }).reduce { acc, s -> "$acc; $s" }
        )

        logger.info("handleMethodArgumentNotValidException [${HttpStatus.BAD_REQUEST}] $ex")

        return ResponseEntity<ExceptionResponse>(exceptionResponse, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(DataIntegrityViolationException::class)
    fun handleDataIntegrityViolationException(
        ex: DataIntegrityViolationException, request: WebRequest
    ): ResponseEntity<ExceptionResponse> {
        val exceptionResponse = ExceptionResponse(
            LocalDateTime.now(),
            "Exclusive Data Restriction",
            "Could Not Execute Statement. Check Provided Payload"
        )

        logger.info("handleDataIntegrityViolationException [${HttpStatus.BAD_REQUEST}] $ex")

        return ResponseEntity<ExceptionResponse>(exceptionResponse, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleHttpMessageNotReadableException(
        ex: HttpMessageNotReadableException, request: WebRequest
    ): ResponseEntity<ExceptionResponse> {
        val exceptionResponse = ExceptionResponse(
            LocalDateTime.now(),
            "Http Message Not Readable",
            ex.message ?: request.getDescription(false)
        )

        logger.info("HttpMessageNotReadableException [${HttpStatus.BAD_REQUEST}] $ex")

        return ResponseEntity<ExceptionResponse>(exceptionResponse, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(MissingPathVariableException::class)
    fun handleMissingPathVariableException(
        ex: MissingPathVariableException, request: WebRequest
    ): ResponseEntity<ExceptionResponse> {
        val exceptionResponse = ExceptionResponse(
            LocalDateTime.now(),
            "Missing Path Variable",
            ex.message
        )

        logger.info("MissingPathVariableException [${HttpStatus.BAD_REQUEST}] $ex")

        return ResponseEntity<ExceptionResponse>(exceptionResponse, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(NoResourceFoundException::class)
    fun handleNoResourceFoundException(
        ex: NoResourceFoundException, request: WebRequest
    ): ResponseEntity<ExceptionResponse> {
        val exceptionResponse = ExceptionResponse(
            LocalDateTime.now(),
            "No Resource Found",
            ex.message ?: request.getDescription(false)
        )

        logger.info("MissingPathVariableException [${HttpStatus.NOT_FOUND}] $ex")

        return ResponseEntity<ExceptionResponse>(exceptionResponse, HttpStatus.NOT_FOUND)
    }

}