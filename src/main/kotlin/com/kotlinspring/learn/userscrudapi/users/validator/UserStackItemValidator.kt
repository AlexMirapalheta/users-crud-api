package com.kotlinspring.learn.userscrudapi.users.validator

import jakarta.validation.Constraint
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import jakarta.validation.Payload
import kotlin.reflect.KClass

@Target(AnnotationTarget.FIELD)
@MustBeDocumented
@Constraint(validatedBy = [UserStackItemValidator::class])
annotation class UserStackItem (
    val message: String = "The user stack item size should be between 1 e {size} characters",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = [],
    val size: Int = 32
)

class UserStackItemValidator : ConstraintValidator<UserStackItem, List<String>> {

    private val size: Int = 32

    override fun isValid(item: List<String>?, p1: ConstraintValidatorContext?): Boolean {
        return item == null || item.all { it.isNotBlank() && it.length <= size }
    }

}