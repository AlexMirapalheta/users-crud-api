package com.kotlinspring.learn.userscrudapi.users.controller

import com.kotlinspring.learn.userscrudapi.users.dto.UserDTO
import com.kotlinspring.learn.userscrudapi.users.service.UserService
import java.util.UUID
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
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

    @GetMapping("/{id}")
    fun findById(
            @Valid @PathVariable(value = "id") id: UUID
    ): UserDTO {
        return service.findById(id)
    }

    @GetMapping
    fun find(
            @Valid @RequestParam(value = "page", defaultValue = "0") page: Int,
            @Valid @RequestParam(value = "size", defaultValue = "50") size: Int
    ): Page<UserDTO> {
        return service.find(page, size)
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional
    fun create(
            @Valid @RequestBody userDto: UserDTO
    ): UserDTO {
        return service.create(userDto)
    }

    @PutMapping("/{id}")
    @Transactional
    fun update(
            @Valid @RequestBody userDto: UserDTO,
            @Valid @PathVariable(value = "id") id: UUID
    ): UserDTO {
        return service.update(id, userDto)
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