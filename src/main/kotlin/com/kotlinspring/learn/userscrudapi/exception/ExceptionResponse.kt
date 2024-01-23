package com.kotlinspring.learn.userscrudapi.exception

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

class ExceptionResponse(
        @JsonProperty("error_timestamp")
        val timestamp: LocalDateTime,

        @JsonProperty("error_message")
        val message: String?,

        @JsonProperty("error_details")
        val details: String
) {
    override fun toString(): String {
        return "ExceptionResponse(error_timestamp=$timestamp, error_message=$message, error_details='$details')"
    }
}