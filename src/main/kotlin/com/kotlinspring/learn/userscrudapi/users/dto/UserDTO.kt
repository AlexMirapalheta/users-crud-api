package com.kotlinspring.learn.userscrudapi.users.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.kotlinspring.learn.userscrudapi.users.validator.UserStackItem
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDateTime
import java.util.UUID

data class UserDTO (
    val id: UUID ? = null,

    @JsonProperty(required = false)
    @field:Size(min = 1, max = 32, message = "Nickname should have min=1 and max=32 characters")
    val nick: String ?,

    @JsonProperty(required = true)
    @field:NotBlank(message = "Name should not be Blank")
    @field:Size(min = 1, max = 255, message = "Name should have min=1 and max=255 characters")
    val name: String,

    @JsonProperty(required = true)
    @field:DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    val birthDate: LocalDateTime,

    @JsonProperty(required = false)
    @field:UserStackItem
    val stack: List<String> ? = null
)