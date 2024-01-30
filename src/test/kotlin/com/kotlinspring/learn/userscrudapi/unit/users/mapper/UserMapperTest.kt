package com.kotlinspring.learn.userscrudapi.unit.users.mapper

import com.kotlinspring.learn.userscrudapi.mock.UserMock
import com.kotlinspring.learn.userscrudapi.users.dto.UserDTO
import com.kotlinspring.learn.userscrudapi.users.entity.User
import com.kotlinspring.learn.userscrudapi.users.mapper.UserMapper
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import java.util.*

@ExtendWith(MockitoExtension::class)
class UserMapperTest {

    private var userMock: UserMock = UserMock()
    private var mapper: UserMapper = UserMapper()

    private var userEntity: User = userMock.createUserEntity()
    private var userDto: UserDTO = userMock.createUserDTO()

    @AfterEach
    fun tearDown() {
        userEntity = userMock.createUserEntity()
        userDto = userMock.createUserDTO()
    }

    @Test
    fun `GIVEN UserDTO, WHEN use parseToEntity, THEN return User Entity`() {
        val result = mapper.parseToEntity(userDto)

        assertInstanceOf(User::class.java, result)
        assertEquals(null, result.id)
        assertEquals(userDto.nick, result.nick)
        assertEquals(userDto.name, result.fullName)
        assertEquals(userDto.birthDate, result.birthDate)
        assertEquals(userDto.stack, result.stack)
    }

    @Test
    fun `GIVEN UserDTO and UUID, WHEN use parseToEntity, THEN return User Entity`() {
        val id: UUID = UUID.randomUUID()
        val result = mapper.parseToEntity(userDto, id)

        assertInstanceOf(User::class.java, result)
        assertEquals(id, result.id)
        assertEquals(userDto.nick, result.nick)
        assertEquals(userDto.name, result.fullName)
        assertEquals(userDto.birthDate, result.birthDate)
        assertEquals(userDto.stack, result.stack)
    }

    @Test
    fun `GIVEN User Entity, WHEN use parseToDTO, THEN return UserDTO`() {
        val result = mapper.parseToDTO(userEntity)

        assertInstanceOf(UserDTO::class.java, result)
        assertEquals(userEntity.id, result.id)
        assertEquals(userEntity.nick, result.nick)
        assertEquals(userEntity.fullName, result.name)
        assertEquals(userEntity.birthDate, result.birthDate)
        assertEquals(userEntity.stack, result.stack)
    }
}