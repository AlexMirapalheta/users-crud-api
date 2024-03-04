package com.kotlinspring.learn.userscrudapi.users.dto

import java.time.LocalDateTime
import java.util.UUID

data class UserResponse (
    val id: UUID,
    val nick: String ?,
    val name: String,
    val birthDate: LocalDateTime,
    val stack: MutableSet<StackResponse> = mutableSetOf()
)