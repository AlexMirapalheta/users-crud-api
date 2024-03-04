package com.kotlinspring.learn.userscrudapi.users.validator

import com.kotlinspring.learn.userscrudapi.users.dto.StackRequest
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*

class UserStackItemValidatorTest {

    private val validator = UserStackItemValidator()

    @Nested
    inner class IsValid {

        @Test
        @DisplayName("GIVEN: valid String (1<=size<=32) stack items list; WHEN: checked by UserStackItemValidator.isValid; THEN: return true")
        fun validStringTest() {
            val stackList: MutableSet<StackRequest> = mutableSetOf()
            stackList.add(StackRequest(stack = "NodeJS", score = 70))
            stackList.add(StackRequest(stack = "SpringBoot", score = 20))
            stackList.add(StackRequest(stack = "That'sStringContains32Characters", score = 100))
            stackList.add(StackRequest(stack = "R", score = 1))

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
            val stackList: MutableSet<StackRequest> = mutableSetOf()
            val result: Boolean = validator.isValid(stackList, null)
            assertFalse(result)
        }

        @Test
        @DisplayName("GIVEN: invalid (blank) stack item list; WHEN: checked by UserStackItemValidator.isValid; THEN: return false")
        fun invalidBlankTest() {
            val stackList: MutableSet<StackRequest> = mutableSetOf()
            stackList.add(StackRequest(stack = " ", score = 100))

            val result: Boolean = validator.isValid(stackList, null)
            assertFalse(result)
        }

        @Test
        @DisplayName("GIVEN: invalid String (size>32) stack item list; WHEN: checked by UserStackItemValidator.isValid; THEN: return false")
        fun invalidStringTest() {
            val stackList: MutableSet<StackRequest> = mutableSetOf()
            stackList.add(StackRequest(stack = "That'sStringContainMoreThan32Characters", score = 100))

            val result: Boolean = validator.isValid(stackList, null)
            assertFalse(result)
        }
    }
}