package com.kotlinspring.learn.userscrudapi.users.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.kotlinspring.learn.userscrudapi.users.validator.UserStackItem
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDateTime
import java.util.UUID

data class UserDTO (
    var id: UUID ? = null,

    @JsonProperty(required = false)
    @field:Size(max = 32, message = "Nickname should have max=32 characters")
    var nick: String ?,

    @JsonProperty(required = true)
    @field:NotBlank(message = "Name should not be Blank")
    @field:Size(min = 1, max = 255, message = "Name should have min=1 and max=255 characters")
    var name: String,

    @JsonProperty("birth_date", required = true)
    @field:DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    var birthDate: LocalDateTime,

    @JsonProperty(required = false)
    @field:UserStackItem
    var stack: List<String> ? = null
) {}