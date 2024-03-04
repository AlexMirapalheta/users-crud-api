package com.kotlinspring.learn.userscrudapi.users.dto

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.hibernate.validator.constraints.Range

data class StackRequest (
    @JsonProperty(required = true)
    @field:NotBlank(message = "Stack name should not be Blank")
    @field:Size(min = 1, max = 32, message = "Stack name should have min=1 and max=32 characters")
    val stack: String,

    @JsonProperty("level", required = true)
    @field:NotNull
    @field:Range(min = 1, max = 100)
    val score: Int,
) {
    override fun toString(): String {
        return "StackRequest(stack='$stack', score=$score)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as StackRequest

        if (stack != other.stack) return false
        if (score != other.score) return false

        return true
    }

    override fun hashCode(): Int {
        var result = stack.hashCode()
        result = 31 * result + score
        return result
    }
}