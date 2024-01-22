package com.kotlinspring.learn.userscrudapi

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class UsersCrudApiApplication

fun main(args: Array<String>) {
	runApplication<UsersCrudApiApplication>(*args)
}
