package com.kotlinspring.learn.userscrudapi.users.mapper

import com.kotlinspring.learn.userscrudapi.mock.UserMock
import com.kotlinspring.learn.userscrudapi.users.dto.UserRequest
import com.kotlinspring.learn.userscrudapi.users.dto.UserResponse
import com.kotlinspring.learn.userscrudapi.users.entity.User
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class UserMapperTest {

    private var userMock: UserMock = UserMock()
    private var mapper: UserMapper = UserMapper()

    private var userEntity: User = userMock.createUserEntity()
    private var userRequest: UserRequest = userMock.createUserRequest()

    @AfterEach
    fun tearDown() {
        userEntity = userMock.createUserEntity()
        userRequest = userMock.createUserRequest()
    }

    @Test
    fun `GIVEN UserRequest, WHEN use parseRequestToEntity, THEN return User Entity`() {
        val result = mapper.parseRequestToEntity(userRequest)

        assertInstanceOf(User::class.java, result)
        assertEquals(null, result.id)
        assertEquals(userRequest.nick, result.nick)
        assertEquals(userRequest.name, result.name)
        assertEquals(userRequest.birthDate, result.birthDate)
    }

//    @Test
//    fun `GIVEN UserRequest and UUID, WHEN use parseRequestToEntity, THEN return User Entity`() {
//        val id: UUID = UUID.randomUUID()
//        val result = mapper.parseRequestToEntity(userRequest, id)
//
//        assertInstanceOf(User::class.java, result)
//        assertEquals(id, result.id)
//        assertEquals(userDto.nick, result.nick)
//        assertEquals(userDto.name, result.name)
//        assertEquals(userDto.birthDate, result.birthDate)
//        assertEquals(userDto.stack, result.stack)
//    }

    @Test
    fun `GIVEN User Entity, WHEN use parseEntityToResponse, THEN return UserResponse`() {
        val result = mapper.parseEntityToResponse(userEntity)

        assertInstanceOf(UserResponse::class.java, result)
        assertEquals(userEntity.id, result.id)
        assertEquals(userEntity.nick, result.nick)
        assertEquals(userEntity.name, result.name)
        assertEquals(userEntity.birthDate, result.birthDate)
    }
}