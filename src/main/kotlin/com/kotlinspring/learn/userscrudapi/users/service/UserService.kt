package com.kotlinspring.learn.userscrudapi.users.service

import com.kotlinspring.learn.userscrudapi.users.dto.UserRequest
import com.kotlinspring.learn.userscrudapi.users.entity.Stack
import com.kotlinspring.learn.userscrudapi.users.entity.User
import com.kotlinspring.learn.userscrudapi.users.exception.UserNotFoundException
import com.kotlinspring.learn.userscrudapi.users.helper.SortHelper
import com.kotlinspring.learn.userscrudapi.users.repository.UserRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import java.util.UUID
import java.util.logging.Logger

@Service
class UserService(
    private var repository: UserRepository
) {

    private var sortHelper: SortHelper = SortHelper()
    private val logger = Logger.getLogger(UserService::class.java.name)

    fun find(page: Int, size: Int, sort: String): Page<User> {
        logger.info("Finding All Users")

        val paging: PageRequest = PageRequest.of(page, size, sortHelper.getSortByString(sort))
        return repository.findAll(paging)
    }

    fun findById(id: UUID): User {
        logger.info("Finding User By ID : $id")

        return repository.findById(id).orElseThrow { UserNotFoundException() }
    }

    fun create(userRequest: UserRequest): User {
        logger.info("Creating $userRequest")

        val newUser = User(
            nick = userRequest.nick,
            name = userRequest.name,
            birthDate = userRequest.birthDate
        )
        userRequest.stack?.forEach {
            newUser.stack.add(
                Stack(
                    stack = it.stack,
                    score = it.score,
                    user = newUser
                )
            )
        }

        return repository.save(newUser)
    }

    fun update(id: UUID, userRequest: UserRequest): User {
        logger.info("Updating $id - $userRequest")

        val user: User = repository.findById(id).orElseThrow { UserNotFoundException() }

        val stackUpdated: MutableSet<Stack> = mutableSetOf()
        userRequest.stack?.forEach {
            val existStack: Stack ? = user.stack.find { exist: Stack -> exist.stack == it.stack }
            stackUpdated.add(
                Stack(
                    existStack?.id,
                    it.stack,
                    it.score,
                    user
                )
            )
        }

        return repository.save(
            user.copy(
                nick = userRequest.nick,
                name = userRequest.name,
                birthDate = userRequest.birthDate,
                stack = stackUpdated
            )
        )
    }

    fun delete(id: UUID) {
        val user: User = repository.findById(id).orElseThrow { UserNotFoundException() }
        logger.info("Deleting $user")
        repository.delete(user)
    }

}