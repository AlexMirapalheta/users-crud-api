package com.kotlinspring.learn.userscrudapi.users.dto

data class FindUsersResponse<T> (
    val records: List<T>,
    val page: Int,
    val pageSize: Int,
    val total: Long
)