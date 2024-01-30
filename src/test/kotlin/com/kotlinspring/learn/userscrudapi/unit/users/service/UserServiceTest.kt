package com.kotlinspring.learn.userscrudapi.unit.users.service

import com.kotlinspring.learn.userscrudapi.mock.UserMock
import com.kotlinspring.learn.userscrudapi.users.dto.UserDTO
import com.kotlinspring.learn.userscrudapi.users.entity.User
import com.kotlinspring.learn.userscrudapi.users.repository.UserRepository
import com.kotlinspring.learn.userscrudapi.users.service.UserService
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.whenever
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import java.util.Optional
import java.util.UUID

@ExtendWith(MockitoExtension::class)
class UserServiceTest {

    @InjectMocks
    private lateinit var service: UserService
    @Mock
    private lateinit var repository: UserRepository

    private var userMock: UserMock = UserMock()

    @Test
    @DisplayName("GIVEN: one User on database; WHEN: UserService.find(); THEN: return all users (just one)")
    fun findTest() {
        val pageNumber = 0
        val pageSize = 50
        val users = userMock.createUserEntityList()
        val pageUsers: Page<User> = PageImpl(users)
        val paging: PageRequest = PageRequest.of(pageNumber, pageSize)

        doReturn(pageUsers).`when`(repository).findAll(paging)
        val result: Page<UserDTO> = service.find(pageNumber, pageSize)

        assertEquals(users.size, result.content.size)
        assertInstanceOf(UserDTO::class.java, result.content[0])
        assertEquals(users[0].nick, result.content[0].nick)
        assertEquals(users[0].fullName, result.content[0].name)
        assertEquals(users[0].birthDate, result.content[0].birthDate)
        assertEquals(users[0].stack, result.content[0].stack)
    }

    @Test
    @DisplayName("GIVEN: valid UUID; WHEN: UserService.findById(UUID); THEN: return UserDTO with requested ID")
    fun findByIdTest() {
        val userEntity = userMock.createUserEntity()
        val optionalUser: Optional<User> = Optional.of(userEntity)

        whenever(repository.findById(any())).thenReturn(optionalUser)
        val result: UserDTO = service.findById(userEntity.id!!)

        assertInstanceOf(UserDTO::class.java, result)
        assertEquals(userEntity.id, result.id)
        assertEquals(userEntity.nick, result.nick)
        assertEquals(userEntity.fullName, result.name)
        assertEquals(userEntity.birthDate, result.birthDate)
        assertEquals(userEntity.stack, result.stack)
    }

    @Test
    @DisplayName("GIVEN:valid UserDTO; WHEN: use UserService.create(userDto); THEN: return UserDTO with ID")
    fun createTest() {
        val id: UUID = UUID.randomUUID()
        val userDto = userMock.createUserDTO()
        val userEntity = User (
            id = id,
            nick = userDto.nick,
            fullName = userDto.name,
            birthDate = userDto.birthDate,
            stack = userDto.stack
        )

        doReturn(userEntity).`when`(repository).save(userEntity.copy(id = null))
        val result: UserDTO = service.create(userDto)

        assertInstanceOf(UserDTO::class.java, result)
        assertEquals(id, result.id)
        assertEquals(userDto.nick, result.nick)
        assertEquals(userDto.name, result.name)
        assertEquals(userDto.birthDate, result.birthDate)
        assertEquals(userDto.stack, result.stack)
    }

    @Test
    @DisplayName("GIVEN: valid UserDTO and Id; WHEN: use UserService.update(Id, userDto); THEN: return updated UserDTO")
    fun updateTest() {
        val id: UUID = UUID.randomUUID()
        val userDto = userMock.createUserDTO()
        val existUserEntity = userMock.createUserEntity().copy(id = id)
        val updatedUserEntity = existUserEntity.copy(
            nick = userDto.nick,
            fullName = userDto.name,
            birthDate = userDto.birthDate,
            stack = userDto.stack
            )

        doReturn(Optional.of(existUserEntity)).`when`(repository).findById(id)
        doReturn(updatedUserEntity).`when`(repository).save(updatedUserEntity)
        val result: UserDTO = service.update(id, userDto)

        assertNotEquals(userDto.nick, existUserEntity.nick)
        assertNotEquals(userDto.name, existUserEntity.fullName)
        assertNotEquals(userDto.birthDate, existUserEntity.birthDate)
        assertNotEquals(userDto.stack, existUserEntity.stack)

        assertInstanceOf(UserDTO::class.java, result)
        assertEquals(id, result.id)
        assertEquals(userDto.nick, result.nick)
        assertEquals(userDto.name, result.name)
        assertEquals(userDto.birthDate, result.birthDate)
        assertEquals(userDto.stack, result.stack)
    }

}