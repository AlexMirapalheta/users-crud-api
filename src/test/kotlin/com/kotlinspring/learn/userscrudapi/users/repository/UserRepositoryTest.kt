package com.kotlinspring.learn.userscrudapi.users.repository

import com.kotlinspring.learn.userscrudapi.users.entity.Stack
import com.kotlinspring.learn.userscrudapi.users.entity.User
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.orm.jpa.JpaSystemException
import java.time.LocalDateTime

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserRepositoryTest {

    @Autowired
    private lateinit var repository: UserRepository

    @Autowired
    private lateinit var jdbcTemplate: JdbcTemplate

    @AfterEach
    fun tearDown() {
        jdbcTemplate.execute("DELETE FROM user_stack")
        jdbcTemplate.execute("DELETE FROM users")
    }

    @Test
    @DisplayName("GIVEN: User with invalid name (size>255); WHEN: UserRepository.create(); THEN: returns an error (JpaSystemException)")
    fun invalidNameSizeCreateTest() {
        val user = User(
            nick = "SomeNick",
            name = "This_Sentence_Written_in_the_English_Language_Was_Designed_and_Written_to_Test_a_String_Type_Input_with_"+
                    "a_Maximum_Allowed_Size_of_255_Characters_Therefore,_To_Meet_the_Premise_of_the_Test,_The_Total_"+
                    "Number_of_Letters_in_This_Sentence_Exceeds_the_Previously_Quoted_Value",
            birthDate = LocalDateTime.now()
        )
        user.stack.add(
            Stack(
                stack = "SomeLanguage",
                score = 75,
                user = user
            )
        )

        assertThrows<JpaSystemException> {
            repository.save(user)
        }
    }

    @Test
    @DisplayName("GIVEN: User with invalid name (not unique); WHEN: UserRepository.create(); THEN: returns an error (DataIntegrityViolationException)")
    fun uniqueNameCreateTest() {
        val userOne = User(
            nick = "SomeNick",
            name = "SomeName",
            birthDate = LocalDateTime.now()
        )
        userOne.stack.add(
            Stack(
                stack = "SomeLanguage",
                score = 75,
                user = userOne
            )
        )
        repository.save(userOne)

        val userTwo = User(
            nick = "AnotherNick",
            name = userOne.name,
            birthDate = LocalDateTime.now()
        )
        userTwo.stack.add(
            Stack(
                stack = "AnotherLanguage",
                score = 70,
                user = userTwo
            )
        )

        assertThrows<DataIntegrityViolationException> {
            repository.save(userTwo)
        }
    }

    @Test
    @DisplayName("GIVEN: User with invalid nick (size>32); WHEN: UserRepository.create(); THEN: returns an error (JpaSystemException)")
    fun invalidNickSizeCreateTest() {
        val user = User(
            nick = "That'sStringContainMoreThan32Characters",
            name = "SomeName",
            birthDate = LocalDateTime.now()
        )
        user.stack.add(
            Stack(
                stack = "SomeLanguage",
                score = 75,
                user = user
            )
        )

        assertThrows<JpaSystemException> {
            repository.save(user)
        }
    }

    @Test
    @DisplayName("GIVEN: User with invalid stack item (size>32); WHEN: UserRepository.create(); THEN: returns an error (JpaSystemException)")
    fun invalidStackItemSizeCreateTest() {
        val user = User(
            nick = "SomeNick",
            name = "SomeName",
            birthDate = LocalDateTime.now(),
        )
        user.stack.add(
            Stack(
                stack = "That'sStringContainMoreThan32Characters",
                score = 70,
                user = user
            )
        )

        assertThrows<JpaSystemException> {
            repository.save(user)
        }
    }
}