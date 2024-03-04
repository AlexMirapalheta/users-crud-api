package com.kotlinspring.learn.userscrudapi.mock

import com.fasterxml.jackson.annotation.JsonProperty
import com.kotlinspring.learn.userscrudapi.users.dto.StackRequest
import com.kotlinspring.learn.userscrudapi.users.dto.StackResponse
import com.kotlinspring.learn.userscrudapi.users.dto.UserRequest
import com.kotlinspring.learn.userscrudapi.users.dto.UserResponse
import com.kotlinspring.learn.userscrudapi.users.entity.Stack
import com.kotlinspring.learn.userscrudapi.users.entity.User
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

data class UserPayload (
    var id: String ? = null,
    var nick: String ?,
    var name: String,
    @JsonProperty("birth_date")
    var birthDate: String,
    var stack: MutableSet<StackRequest> = mutableSetOf()
)

@Component
class UserMock {
    val maxSize = 10000
    val maxLevel: Int = 100
    val maxYear: Int = 99

    fun getRandomInt(maxValue: Int = maxSize): Int {
        return (Math.random()*maxValue).toInt()
    }

    fun getRandomLong(maxValue: Int = maxSize): Long {
        return getRandomInt(maxValue).toLong()
    }

    fun getRandomYear(maxValue: Int = maxYear, stringLength: Int = 2, stringPadChar: Char = '0'): String {
        return (Math.random()*maxValue).toInt().toString().padStart(stringLength, stringPadChar)
    }

    fun createUserPayload(): UserPayload {
        val rnd = getRandomInt()

        val newUser = UserPayload(
            nick = "nick $rnd",
            name = "Full Name $rnd",
            birthDate = "19${getRandomYear()}-10-17T09:40:01"
        )
        newUser.stack.add(StackRequest(
            stack = "NodeJS $rnd",
            score = getRandomInt(maxLevel)
        ))
        newUser.stack.add(StackRequest(
            stack = "Kotlin $rnd",
            score = getRandomInt(maxLevel)
        ))

        return newUser
    }

    fun createUserRequest(): UserRequest {
        val rnd = getRandomInt()

        val newUser = UserRequest(
            nick = "nick $rnd",
            name = "Full Name $rnd",
            birthDate = LocalDateTime.parse(
                "19${getRandomYear()}-10-17T09:40:01",
                DateTimeFormatter.ofPattern("yyyy-MM-dd"+"'T'"+"HH:mm:ss")
            )
        )
        newUser.stack!!.add(StackRequest(
            stack = "NodeJS $rnd",
            score = getRandomInt(maxLevel)
        ))
        newUser.stack!!.add(StackRequest(
            stack = "Kotlin $rnd",
            score = getRandomInt(maxLevel)
        ))

        return newUser
    }

    fun createUserEntity(withId: Boolean = true): User {
        val rnd = getRandomInt()

        val newUser = User(
            id = if (withId) UUID.randomUUID() else null,
            nick = "nick $rnd",
            name = "Full Name $rnd",
            birthDate = LocalDateTime.parse(
                "19${getRandomYear()}-10-17T09:40:00",
                DateTimeFormatter.ofPattern("yyyy-MM-dd"+"'T'"+"HH:mm:ss")
            ),
        )
        newUser.stack.add(
            Stack(
            id = if (withId) getRandomLong() else null,
            stack = "NodeJS $rnd",
            score = getRandomInt(maxLevel),
            user = newUser
            )
        )
        newUser.stack.add(
            Stack(
                id = getRandomLong(),
                stack = "Kotlin $rnd",
                score = getRandomInt(maxLevel),
                user = newUser
            )
        )

        return newUser
    }

    fun createUserEntityList(quantity: Int = 1): List<User> {
        val users: List<User> = ArrayList()

        for (i in 1..quantity) {
            val user = this.createUserEntity()
            users.addLast(user)
        }

        return users
    }

    fun createUserResponse(): UserResponse {
        val rnd = getRandomInt()

        val newUser = UserResponse(
            id = UUID.randomUUID(),
            nick = "nick $rnd",
            name = "Full Name $rnd",
            birthDate = LocalDateTime.parse("19${getRandomYear()}-10-17T09:40:00", DateTimeFormatter.ofPattern("yyyy-MM-dd"+"'T'"+"HH:mm:ss"))
        )
        newUser.stack.add(
            StackResponse(
                stack = "NodeJS $rnd",
                level = getRandomInt(maxLevel)
            )
        )
        newUser.stack.add(
            StackResponse(
                stack = "Kotlin $rnd",
                level = getRandomInt(maxLevel)
            )
        )

        return newUser
    }

}