package com.byeduck.webfluxrest.handlers

import com.byeduck.webfluxrest.domain.Category
import com.byeduck.webfluxrest.repositories.CategoryRepository
import com.byeduck.webfluxrest.validators.Validator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.*
import org.springframework.web.reactive.function.server.body
import reactor.core.publisher.Mono
import reactor.core.publisher.toMono
import java.net.URI

@Component
class CategoryHandler(
        @Autowired private val categoryRepository: CategoryRepository,
        @Autowired private val categoryValidator: Validator<Category>
) {

    fun get(request: ServerRequest): Mono<ServerResponse> = request.queryParam("desc")
            .map {
                categoryRepository.findByDescription(it)
                        .flatMap { c -> ok().body(c.toMono()) }
                        .switchIfEmpty(notFound().build())
            }.orElse(ok().body(categoryRepository.findAll()))

    fun getById(request: ServerRequest): Mono<ServerResponse> = categoryRepository
            .findById(request.pathVariable("id"))
            .flatMap { ok().body(it.toMono()) }
            .switchIfEmpty(notFound().build())

    fun getByDescription(request: ServerRequest): Mono<ServerResponse> = categoryRepository
            .findByDescription(request.queryParam("desc")
                    .filter { it != null }
                    .orElse(""))
            .flatMap { ok().body(it.toMono()) }
            .switchIfEmpty(notFound().build())

    fun add(request: ServerRequest): Mono<ServerResponse> = categoryRepository
            .saveAll(categoryValidator.validate(request.bodyToMono(Category::class.java)))
            .flatMap { created(URI.create("categories/${it.id}")).body(it.toMono()) }
            .switchIfEmpty(badRequest().body(Mono.just("Description cannot be blank."))).toMono()

    fun updateById(request: ServerRequest): Mono<ServerResponse> = categoryRepository
            .findById(request.pathVariable("id"))
            .flatMap {
                ok().body(
                        categoryValidator
                                .validate(request.bodyToMono(Category::class.java))
                                .map { Mono.just(Category(it.description, request.pathVariable("id"))) }
                                .flatMap { categoryRepository.saveAll(it).toMono() })
            }.switchIfEmpty(notFound().build())

    fun deleteById(request: ServerRequest): Mono<ServerResponse> = categoryRepository
            .deleteById(request.pathVariable("id"))
            .flatMap { ok().build() }
}