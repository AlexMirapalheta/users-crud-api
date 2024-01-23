package com.kotlinspring.learn.userscrudapi.handler

import com.kotlinspring.learn.userscrudapi.exception.ExceptionResponse
import com.kotlinspring.learn.userscrudapi.users.exception.UserNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.context.request.WebRequest
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import java.time.LocalDateTime

@ControllerAdvice
@RestController
class ExceptionHandler : ResponseEntityExceptionHandler() {
    @ExceptionHandler(Exception::class)
    fun handleAllExceptions (ex: Exception, request: WebRequest): ResponseEntity<ExceptionResponse> {
        val exceptionResponse = ExceptionResponse(
            LocalDateTime.now(),
            ex.message,
            request.getDescription(false)
        )

        logger.info("handleAllExceptions [${HttpStatus.INTERNAL_SERVER_ERROR}] $ex")

        return ResponseEntity<ExceptionResponse>(exceptionResponse, HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @ExceptionHandler(UserNotFoundException::class)
    fun handleUserNotFoundExceptions (ex: Exception, request: WebRequest): ResponseEntity<ExceptionResponse> {
        val exceptionResponse = ExceptionResponse(
            LocalDateTime.now(),
            ex.message,
            request.getDescription(false)
        )

        logger.info("handleUserNotFoundExceptions [${HttpStatus.NOT_FOUND}] $ex")

        return ResponseEntity<ExceptionResponse>(exceptionResponse, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun handleValidationException (ex: MethodArgumentTypeMismatchException, request: WebRequest): ResponseEntity<ExceptionResponse> {
        val exceptionResponse = ExceptionResponse(
            LocalDateTime.now(),
            ex.message,
            request.getDescription(false)
        )

        logger.info("handleValidationException [${HttpStatus.BAD_REQUEST}] $ex")

        return ResponseEntity<ExceptionResponse>(exceptionResponse, HttpStatus.BAD_REQUEST)
    }

//    @ExceptionHandler(MethodArgumentNotValidException::class)
//    fun handleMethodArguentNotValidException(
//            req: HttpServletRequest,
//            ex: MethodArgumentNotValidException
//    ): ResponseEntity<ExceptionResponse> {
//        val exceptionResponse = ExceptionResponse(
//                LocalDateTime.now(),
//                ex.message,
//                (ex.bindingResult.allErrors.map { it.defaultMessage ?: "" }).toString()
//        )
//
//        logger.info("handleMethodArguentNotValidException [${HttpStatus.BAD_REQUEST}] $ex")
//
//        return ResponseEntity<ExceptionResponse>(exceptionResponse, HttpStatus.BAD_REQUEST)
//    }

}