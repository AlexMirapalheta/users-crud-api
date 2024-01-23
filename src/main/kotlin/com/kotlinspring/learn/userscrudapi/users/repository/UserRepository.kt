package com.kotlinspring.learn.userscrudapi.users.repository

import com.kotlinspring.learn.userscrudapi.users.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface UserRepository : JpaRepository<User, UUID?>