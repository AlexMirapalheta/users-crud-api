package com.kotlinspring.learn.userscrudapi.users.entity

import jakarta.persistence.*

@Entity
@Table(
    name = "user_stack",
    uniqueConstraints = [ UniqueConstraint(columnNames = ["stack", "user_id"]) ]
)
data class Stack (
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long ? = null,

    @Column(nullable = false, length = 32)
    val stack: String,

    @Column(nullable = false)
    val score: Int,

    @ManyToOne( fetch = FetchType.LAZY )
    @JoinColumn( name = "user_id", nullable = false)
    val user: User?
) {
    override fun toString(): String {
        return "Stack(id=$id, stack='$stack', score=$score)"
    }

    override fun hashCode(): Int {
        if (id !== null) return id.hashCode()
        return super.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Stack

        if (id != other.id) return false
        if (stack != other.stack) return false
        if (score != other.score) return false
        if (user != other.user) return false

        return true
    }
}