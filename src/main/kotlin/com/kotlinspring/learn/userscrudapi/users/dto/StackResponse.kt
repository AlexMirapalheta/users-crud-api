package com.kotlinspring.learn.userscrudapi.users.dto

data class StackResponse (
    val stack: String,
    val level: Int,
) {
    override fun toString(): String {
        return "StackResponse(stack='$stack', level=$level)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as StackResponse

        if (stack != other.stack) return false
        if (level != other.level) return false

        return true
    }

    override fun hashCode(): Int {
        var result = stack.hashCode()
        result = 31 * result + level
        return result
    }

}