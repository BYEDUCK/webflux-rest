package com.byeduck.webfluxrest

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@EnableEncryptableProperties
@SpringBootApplication
class WebfluxRestApplication

fun main(args: Array<String>) {
	runApplication<WebfluxRestApplication>(*args)
}
