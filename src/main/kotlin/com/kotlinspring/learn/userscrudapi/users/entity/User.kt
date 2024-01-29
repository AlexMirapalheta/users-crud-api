package com.kotlinspring.learn.userscrudapi.users.entity

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "users")
data class User (
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID ?,

    @Column(name = "nick", nullable = true, length = 32)
    val nick: String ?,

    @Column(name = "name", nullable = false, length = 255, unique = true)
    val fullName: String,

    @Column(name = "birth_date", nullable = false)
    val birthDate: LocalDateTime,

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name="user_stack",
            joinColumns = [JoinColumn(name = "user_id")],
    )
    @Column(length = 32)
    val stack: List<String>?
) {
    override fun toString(): String {
        return "UserEntity(id=$id, nick=$nick, fullName=$fullName, birthDate=$birthDate, stack=$stack)"
    }
}