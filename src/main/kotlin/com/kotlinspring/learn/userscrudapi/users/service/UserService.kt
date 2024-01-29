package com.kotlinspring.learn.userscrudapi.users.service

import com.kotlinspring.learn.userscrudapi.users.dto.UserDTO
import com.kotlinspring.learn.userscrudapi.users.entity.User
import com.kotlinspring.learn.userscrudapi.users.exception.UserNotFoundException
import com.kotlinspring.learn.userscrudapi.users.mapper.UserMapper
import com.kotlinspring.learn.userscrudapi.users.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import java.util.*
import java.util.logging.Logger
import kotlin.collections.ArrayList


@Service
class UserService(
    private var repository: UserRepository
) {

    private var mapper: UserMapper = UserMapper()
    private val logger = Logger.getLogger(UserService::class.java.name)

    fun find(page: Int = 0, size: Int = 50): Page<UserDTO> {
        logger.info("Finding All Users")

        val paging: PageRequest = PageRequest.of(page, size)
        val users: Page<UserDTO> = repository.findAll(paging).map { user: User -> UserDTO(
                id = user.id,
                name = user.fullName,
                nick = user.nick,
                birthDate = user.birthDate,
                stack = user.stack
        )}
        return users
    }

    fun findById(id: UUID): UserDTO {
        logger.info("Finding User By ID : $id")

        val user: User = repository.findById(id).orElseThrow { UserNotFoundException() }
        return mapper.parseToDTO(user)
    }

    fun create(user: UserDTO): UserDTO {
        logger.info("Creating $user")

        val entity: User = repository.save(mapper.parseToEntity(user))
        return mapper.parseToDTO(entity)
    }

    fun update(id: UUID, userDto: UserDTO): UserDTO {
        logger.info("Updating $userDto")

        val user: User = repository.findById(id).orElseThrow { UserNotFoundException() }
        val userUpdated: User = repository.save(
            user.copy(
                nick = userDto.nick,
                fullName = userDto.name,
                birthDate = userDto.birthDate,
                stack = userDto.stack,
            )
        )

        return mapper.parseToDTO(userUpdated)
    }

    fun delete(id: UUID) {
        val user: User = repository.findById(id).orElseThrow { UserNotFoundException() }
        logger.info("Deleting $user")
        repository.delete(user)
    }

}