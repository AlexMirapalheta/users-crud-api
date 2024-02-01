package com.kotlinspring.learn.userscrudapi.users.validator

import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*

class UserStackItemValidatorTest {

    private val validator = UserStackItemValidator()

    @Nested
    inner class IsValid {

        @Test
        @DisplayName("GIVEN: valid String (1<=size<=32) stack items list; WHEN: checked by UserStackItemValidator.isValid; THEN: return true")
        fun validStringTest() {
            val stackList: List<String> = listOf("NodeJS", "kotlin", "SpringBoot", "That'sStringContains32Characters", "R")
            val result: Boolean = validator.isValid(stackList, null)
            assertTrue(result)
        }

        @Test
        @DisplayName("GIVEN: valid (null) stack items list; WHEN: checked by UserStackItemValidator.isValid; THEN: return true")
        fun validNullTest() {
            val result = validator.isValid(null, null)
            assertTrue(result)
        }

        @Test
        @DisplayName("GIVEN: invalid (empty) stack item list ; WHEN: checked by UserStackItemValidator.isValid; THEN: return false")
        fun invalidEmptyTest() {
            val stackList: List<String> = listOf("")
            val result: Boolean = validator.isValid(stackList, null)
            assertFalse(result)
        }

        @Test
        @DisplayName("GIVEN: invalid (blank) stack item list; WHEN: checked by UserStackItemValidator.isValid; THEN: return false")
        fun invalidBlankTest() {
            val stackList: List<String> = listOf(" ")
            val result: Boolean = validator.isValid(stackList, null)
            assertFalse(result)
        }

        @Test
        @DisplayName("GIVEN: invalid String (size>32) stack item list; WHEN: checked by UserStackItemValidator.isValid; THEN: return false")
        fun invalidStringTest() {
            val stackList: List<String> = listOf("That'sStringContainMoreThan32Characters")
            val result: Boolean = validator.isValid(stackList, null)
            assertFalse(result)
        }
    }
}