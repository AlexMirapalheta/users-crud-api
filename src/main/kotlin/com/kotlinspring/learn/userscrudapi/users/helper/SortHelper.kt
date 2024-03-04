package com.kotlinspring.learn.userscrudapi.users.helper

import org.springframework.data.domain.Sort
import org.springframework.stereotype.Component

@Component
class SortHelper {

    fun getSortByString(sort: String): Sort {
        var direction = "ASC"
        var sortBy: String = sort
        if (sort.startsWith("-")) {
            direction = "DESC"
            sortBy = sort.substring(1)
        }

        return Sort.by(Sort.Direction.fromString(direction), sortBy)
    }
}