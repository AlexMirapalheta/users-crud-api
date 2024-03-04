package com.kotlinspring.learn.userscrudapi.users.controller

import com.kotlinspring.learn.userscrudapi.users.dto.FindUsersResponse
import com.kotlinspring.learn.userscrudapi.users.dto.UserRequest
import com.kotlinspring.learn.userscrudapi.users.dto.UserResponse
import com.kotlinspring.learn.userscrudapi.users.entity.User
import com.kotlinspring.learn.userscrudapi.users.mapper.UserMapper
import com.kotlinspring.learn.userscrudapi.users.service.UserService
import java.util.UUID
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(value = ["/users"])
class UserController {

    @Autowired
    private lateinit var service: UserService

    private val mapper: UserMapper = UserMapper()

    @GetMapping("/{id}")
    fun findById(
            @Valid @PathVariable(value = "id") id: UUID
    ): UserResponse {
        return mapper.parseEntityToResponse(service.findById(id))
    }

    @GetMapping
    fun find(
            @Valid @RequestParam(value = "page", defaultValue = "0")page: Int,
            @Valid @RequestParam(value = "page_size", defaultValue = "15") size: Int,
            @Valid @RequestParam(value = "sort", defaultValue = "id") sort: String
    ): ResponseEntity<FindUsersResponse<UserResponse>> {
        val usersPage: Page<User> = service.find(page, size, sort)
        val responseData = FindUsersResponse(
            records = usersPage.content.map { it: User -> mapper.parseEntityToResponse(it) },
            page = usersPage.pageable.pageNumber,
            pageSize = usersPage.pageable.pageSize,
            total = usersPage.totalElements
        )
        val responseStatus: HttpStatus = if (!usersPage.isLast) HttpStatus.PARTIAL_CONTENT else HttpStatus.OK

        return ResponseEntity.status(responseStatus).body(responseData)
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional
    fun create(
            @Valid @RequestBody userRequest: UserRequest
    ): UserResponse {
        return mapper.parseEntityToResponse(service.create(userRequest))
    }

    @PutMapping("/{id}")
    @Transactional
    fun update(
        @Valid @RequestBody userRequest: UserRequest,
        @Valid @PathVariable(value = "id") id: UUID
    ): UserResponse {
        return mapper.parseEntityToResponse(service.update(id, userRequest))
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    fun delete(
            @Valid @PathVariable(value = "id") id: UUID
    ) {
        service.delete(id)
    }
}