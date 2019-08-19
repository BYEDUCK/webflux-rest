package com.byeduck.webfluxrest.handlers

import com.byeduck.webfluxrest.domain.Category
import com.byeduck.webfluxrest.repositories.CategoryRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.ServerResponse.status
import org.springframework.web.reactive.function.server.body
import reactor.core.publisher.toMono

@Component
class CategoryHandler(@Autowired private val categoryRepository: CategoryRepository) {

    fun getAll(request: ServerRequest) = ok()
            .body(categoryRepository.findAll())

    fun getById(request: ServerRequest) = ok()
            .body(categoryRepository.findById(request.pathVariable("id")))

    fun add(request: ServerRequest) = status(HttpStatus.CREATED)
            .body(categoryRepository.saveAll(request.bodyToMono(Category::class.java)))
            .toMono()

}