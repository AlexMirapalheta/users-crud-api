package com.kotlinspring.learn.userscrudapi.users.entity

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "users")
data class User (
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID ? = null,

    @Column(nullable = true, length = 32)
    val nick: String ?,

    @Column(nullable = false, length = 255, unique = true)
    val name: String,

    @Column(name = "birth_date", nullable = false)
    val birthDate: LocalDateTime,

    @OneToMany(
        mappedBy = "user",
        fetch = FetchType.LAZY,
        cascade = [ CascadeType.ALL ],
        orphanRemoval = true
        )
    val stack: MutableSet<Stack> = mutableSetOf()
) {

    override fun hashCode(): Int {
        if (id !== null) return id.hashCode()
        return super.hashCode()
    }

    override fun toString(): String {
        return "User(id=$id, nick=$nick, name=$name, birthDate=$birthDate, stack=$stack)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as User

        if (id != other.id) return false
        if (nick != other.nick) return false
        if (name != other.name) return false
        if (birthDate != other.birthDate) return false
        if (stack != other.stack) return false

        return true
    }
}