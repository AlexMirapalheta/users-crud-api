package com.kotlinspring.learn.userscrudapi.mock

import com.kotlinspring.learn.userscrudapi.users.dto.UserDTO
import com.kotlinspring.learn.userscrudapi.users.entity.User
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

data class UserPayload (
    var id: String ? = null,
    var nick: String ?,
    var name: String,
    var birth_date: String,
    var stack: List<String> ?
)

@Component
class UserMock {
    private val maxSize = 10000
    private val yearRatio = 100
    private fun getRandomInt(): Int {
        var rnd: Int = (Math.random()*maxSize).toInt()
        if (rnd/yearRatio < 10) rnd += 1000
        if (rnd/yearRatio > 99) rnd -= 100
        return rnd
    }

    fun createUserPayload(): UserPayload {
        val rnd = getRandomInt()

        val stacks: List<String> = ArrayList()
        stacks.addLast("NodeJS $rnd")
        stacks.addLast("Kotlin $rnd")

        return UserPayload(
            nick = "nick $rnd",
            name = "Full Name $rnd",
            birth_date = LocalDateTime.parse(
                "19${rnd/yearRatio}-10-17T09:40:01Z",
                DateTimeFormatter.ofPattern("yyyy-MM-dd"+"'T'"+"HH:mm:ss"+"'Z'")
            ).toString(),
            stack = stacks
        )
    }

    fun createUserEntity(): User {
        val rnd = getRandomInt()

        val stacks: List<String> = ArrayList()
        stacks.addLast("NodeJS $rnd")
        stacks.addLast("Kotlin $rnd")

        return User(
            id = UUID.randomUUID(),
            nick = "nick $rnd",
            fullName = "Full Name $rnd",
            birthDate = LocalDateTime.parse("19${rnd/yearRatio}-10-17T09:40:00Z", DateTimeFormatter.ofPattern("yyyy-MM-dd"+"'T'"+"HH:mm:ss"+"'Z'")),
            stack = stacks
        )
    }

    fun createUserEntityList(quantity: Int = 1): List<User> {
        val users: List<User> = ArrayList()

        for (i in 1..quantity) {
            val user = this.createUserEntity()
            users.addLast(user)
        }

        return users
    }

    fun createUserDTO(): UserDTO {
        val rnd = getRandomInt()

        val stacks: List<String> = ArrayList()
        stacks.addLast("NodeJS $rnd")
        stacks.addLast("Kotlin $rnd")

        return UserDTO(
            nick = "nick $rnd",
            name = "Full Name $rnd",
            birthDate = LocalDateTime.parse("19${rnd/yearRatio}-10-17T09:40:00Z", DateTimeFormatter.ofPattern("yyyy-MM-dd"+"'T'"+"HH:mm:ss"+"'Z'")),
            stack = stacks
        )
    }

}