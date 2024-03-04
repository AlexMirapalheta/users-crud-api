package com.kotlinspring.learn.userscrudapi.exception

import com.fasterxml.jackson.annotation.JsonProperty

class ErrorMessage(
    val code: String,
    val description: String
)

class ExceptionResponse(
        @JsonProperty("error_messages")
        val errorMessages: List<ErrorMessage>,
)