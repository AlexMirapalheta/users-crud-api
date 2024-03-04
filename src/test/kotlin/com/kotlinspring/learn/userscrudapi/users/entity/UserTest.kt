package com.kotlinspring.learn.userscrudapi.users.entity

import com.kotlinspring.learn.userscrudapi.mock.UserMock
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class UserTest {

    private val userMock: UserMock = UserMock()
    private var userEntity: User = userMock.createUserEntity()

    @Test
    fun testToString() {
        val result: String = userEntity.toString()

        val pattern = "^User.*id=${userEntity.id}.*nick=${userEntity.nick}.*name=${userEntity.name}.*birthDate=${userEntity.birthDate}.*stack=.${userEntity.stack}.*$"
        pattern.replace("[","")
        pattern.replace("]","")
        val regex = Regex(pattern)

        Assertions.assertTrue(result.matches(regex))
    }
}