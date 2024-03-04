package com.kotlinspring.learn.userscrudapi.users.service

import com.kotlinspring.learn.userscrudapi.mock.UserMock
import com.kotlinspring.learn.userscrudapi.users.entity.Stack
import com.kotlinspring.learn.userscrudapi.users.entity.User
import com.kotlinspring.learn.userscrudapi.users.repository.UserRepository
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.*
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
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
        val sort = "name"
        val users = userMock.createUserEntityList()
        val pageUsers: Page<User> = PageImpl(users)
        val paging: PageRequest = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.fromString("ASC"), sort))

        doReturn(pageUsers).`when`(repository).findAll(paging)
        val result: Page<User> = service.find(pageNumber, pageSize, sort)

        assertEquals(users.size, result.content.size)
        assertInstanceOf(User::class.java, result.content[0])
        assertEquals(users[0].nick, result.content[0].nick)
        assertEquals(users[0].name, result.content[0].name)
        assertEquals(users[0].birthDate, result.content[0].birthDate)
        assertEquals(users[0].stack, result.content[0].stack)
    }

    @Test
    @DisplayName("GIVEN: valid UUID; WHEN: UserService.findById(UUID); THEN: return UserEntity with requested ID")
    fun findByIdTest() {
        val userEntity = userMock.createUserEntity()
        val optionalUser: Optional<User> = Optional.of(userEntity)

        whenever(repository.findById(any())).thenReturn(optionalUser)
        val result: User = service.findById(userEntity.id!!)

        assertInstanceOf(User::class.java, result)
        assertEquals(userEntity.id, result.id)
        assertEquals(userEntity.nick, result.nick)
        assertEquals(userEntity.name, result.name)
        assertEquals(userEntity.birthDate, result.birthDate)
        assertEquals(userEntity.stack, result.stack)
    }

    @Test
    @DisplayName("GIVEN:valid UserRequest; WHEN: use UserService.create(userRequest); THEN: return User (entity) with ID")
    fun createTest() {
        val id: UUID = UUID.randomUUID()
        val userRequest = userMock.createUserRequest()

//        val userEntityResponse = User (
//            id = id,
//            nick = userRequest.nick,
//            name = userRequest.name,
//            birthDate = LocalDateTime.parse(
//                userRequest.birthDate,
//                DateTimeFormatter.ofPattern("yyyy-MM-dd"+"'T'"+"HH:mm:ss")
//            )
//        )
        val userEntityResponse = User (
            id = id,
            nick = userRequest.nick,
            name = userRequest.name,
            birthDate = userRequest.birthDate
        )
        userRequest.stack?.forEach {
            userEntityResponse.stack.add(
                Stack(
                    id = userMock.getRandomLong(),
                    stack = it.stack,
                    score = it.score,
                    user = userEntityResponse
                )
            )
        }

        /***
         * TODO Lazy solution. Search for something better!
         */
        doReturn(userEntityResponse).`when`(repository).save(any())
        val result: User = service.create(userRequest)

        assertInstanceOf(User::class.java, result)
        assertEquals(id, result.id)
        assertEquals(userRequest.nick, result.nick)
        assertEquals(userRequest.name, result.name)
        assertEquals(userRequest.birthDate, result.birthDate)
        result.stack.forEach {
            val expectedStack: Stack ? = userEntityResponse.stack.find { stack: Stack -> it.id == stack.id }
            assertNotNull(expectedStack)
            assertEquals(expectedStack!!.stack, it.stack)
            assertEquals(expectedStack.score, it.score)
        }

    }

    @Test
    @DisplayName("GIVEN: valid UserRequest and Id; WHEN: use UserService.update(Id, userRequest); THEN: return updated User")
    fun updateTest() {
        val existUserEntity = userMock.createUserEntity()
        val id: UUID = existUserEntity.id!!
        val userRequest = userMock.createUserRequest()

        val updatedStack: MutableSet<Stack> = mutableSetOf()
        userRequest.stack?.forEach {
            updatedStack.add(
                Stack(
                    userMock.getRandomLong(),
                    it.stack,
                    it.score,
                    existUserEntity
                )
            )
        }
        val updatedUserEntity = existUserEntity.copy(
            nick = userRequest.nick,
            name = userRequest.name,
            birthDate = userRequest.birthDate,
            stack = updatedStack
        )

        doReturn(Optional.of(existUserEntity)).`when`(repository).findById(id)
        doReturn(updatedUserEntity).`when`(repository).save(any())
        val result: User = service.update(id, userRequest)

        assertNotEquals(userRequest.nick, existUserEntity.nick)
        assertNotEquals(userRequest.name, existUserEntity.name)
        assertNotEquals(userRequest.birthDate, existUserEntity.birthDate)
        assertNotEquals(userRequest.stack, existUserEntity.stack)

        assertInstanceOf(User::class.java, result)
        assertEquals(id, result.id)
        assertEquals(userRequest.nick, result.nick)
        assertEquals(userRequest.name, result.name)
        assertEquals(userRequest.birthDate, result.birthDate)

        userRequest.stack?.forEach {
            val search: Stack ? = result.stack.find { stk: Stack -> stk.stack === it.stack }
            assertNotNull(search)
        }

    }

}