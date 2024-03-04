package com.kotlinspring.learn.userscrudapi.users.mapper

import com.kotlinspring.learn.userscrudapi.users.dto.StackResponse
import com.kotlinspring.learn.userscrudapi.users.dto.UserRequest
import com.kotlinspring.learn.userscrudapi.users.dto.UserResponse
import com.kotlinspring.learn.userscrudapi.users.entity.Stack
import com.kotlinspring.learn.userscrudapi.users.entity.User
import org.springframework.stereotype.Component

@Component
class UserMapper {

    fun parseRequestToEntity(userRequest: UserRequest): User {
        val newUser = User(
            nick = userRequest.nick,
            name = userRequest.name,
            birthDate = userRequest.birthDate
        )

        userRequest.stack?.forEach {
            newUser.stack.add(
                Stack(
                    id = null,
                    stack = it.stack,
                    score = it.score,
                    user = newUser
                )
            )
        }

        return newUser
    }

    fun parseEntityToResponse(user: User): UserResponse {
        val userStackResponse: MutableSet<StackResponse> = mutableSetOf()
        user.stack.forEach {
            userStackResponse.add(
                StackResponse(
                    stack = it.stack,
                    level = it.score
                )
            )
        }

        return UserResponse(
            id = user.id!!,
            nick = user.nick,
            name = user.name,
            birthDate = user.birthDate,
            stack = userStackResponse
        )
    }

//    fun parseToDTO(user: User): UserResponse {
//        return UserResponse (
//            id = user.id,
//            nick = user.nick,
//            name = user.name,
//            birthDate = user.birthDate,
//            stack = user.stack
//        )
//    }

}