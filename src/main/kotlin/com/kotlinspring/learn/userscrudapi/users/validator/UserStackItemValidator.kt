package com.kotlinspring.learn.userscrudapi.users.validator

import com.kotlinspring.learn.userscrudapi.users.dto.StackRequest
import jakarta.validation.Constraint
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import jakarta.validation.Payload
import kotlin.reflect.KClass

@Target(AnnotationTarget.FIELD)
@MustBeDocumented
@Constraint(validatedBy = [UserStackItemValidator::class])
annotation class UserStackItem (
    val message: String = "The user stack name size should be between 1 e 32 characters, level should be an integer between 1 and 100",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = [],
    val size: Int = 32
)

class UserStackItemValidator : ConstraintValidator<UserStackItem, MutableSet<StackRequest>> {

    private val maxSize: Int = 32
    private val maxScore: Int = 32

    override fun isValid(stack: MutableSet<StackRequest>?, p1: ConstraintValidatorContext?): Boolean {
        return stack == null || (
                stack.isNotEmpty() &&
                stack.all {
                    it.stack.isNotBlank() &&
                    it.stack.length in 1..maxSize &&
                    it.score in 1..maxScore
                }
            )
    }

}