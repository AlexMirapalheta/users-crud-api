package com.kotlinspring.learn.userscrudapi.users.mapper

import com.kotlinspring.learn.userscrudapi.users.dto.UserDTO
import com.kotlinspring.learn.userscrudapi.users.entity.User
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class UserMapper {

    fun parseToEntity(userDto: UserDTO, id: UUID ? = null): User {
        return User(
            id =  id,
            nick = userDto.nick,
            fullName = userDto.name,
            birthDate = userDto.birthDate,
            stack = userDto.stack
        )
    }

    fun parseToDTO(user: User): UserDTO {
        return UserDTO (
            id = user.id,
            nick = user.nick,
            name = user.fullName,
            birthDate = user.birthDate,
            stack = user.stack
        )
    }

}