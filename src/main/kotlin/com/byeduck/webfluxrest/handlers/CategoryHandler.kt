package com.byeduck.webfluxrest.handlers

import com.byeduck.webfluxrest.constants.*
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
import java.util.*

@Component
class CategoryHandler(
        @Autowired private val categoryRepository: CategoryRepository,
        @Autowired private val categoryValidator: Validator<Category>
) {

    fun get(request: ServerRequest): Mono<ServerResponse> = request.queryParam(descriptionQueryParameterName)
            .map { desc ->
                Optional.of(desc).filter { it.isNotBlank() }.map {
                    categoryRepository.findByDescription(desc)
                            .flatMap { c -> ok().body(c.toMono()) }
                            .switchIfEmpty(notFound().build())
                }.orElse(noContent().build())
            }.orElse(Mono.defer { ok().body(categoryRepository.findAll()) })

    fun getById(request: ServerRequest): Mono<ServerResponse> = categoryRepository
            .findById(request.pathVariable(idPathParameterName))
            .flatMap { ok().body(it.toMono()) }
            .switchIfEmpty(noContent().build())

    fun add(request: ServerRequest): Mono<ServerResponse> = categoryValidator
            .validate(request.bodyToMono(Category::class.java))
            .flatMap { c ->
                categoryRepository.findByDescription(c.description)
                        .flatMap { badRequest().body(Mono.just(givenCategoryAlreadyExistsMsg)) }
                        .switchIfEmpty(
                                Mono.defer {
                                    categoryRepository
                                            .saveAll(c.toMono())
                                            .flatMap {
                                                created(URI.create("$baseCategoriesUrl/${it.id}")).body(it.toMono())
                                            }.toMono()
                                }
                        )
            }.switchIfEmpty(badRequest().body(Mono.just(blankCategoryDescriptionMsg)))

    fun updateById(request: ServerRequest): Mono<ServerResponse> = categoryValidator
            .validate(request.bodyToMono(Category::class.java))
            .flatMap { c ->
                categoryRepository.findById(request.pathVariable(idPathParameterName))
                        .flatMap {
                            categoryRepository.findByDescription(c.description)
                                    .flatMap { badRequest().body(Mono.just(givenCategoryAlreadyExistsMsg)) }
                                    .switchIfEmpty(Mono.defer {
                                        ok().body(categoryRepository.saveAll(
                                                Mono.just(Category(c.description, request.pathVariable(idPathParameterName)))
                                        ).toMono())
                                    })
                        }.switchIfEmpty(notFound().build())
            }.switchIfEmpty(badRequest().body(Mono.just(blankCategoryDescriptionMsg)))

    fun deleteById(request: ServerRequest): Mono<ServerResponse> = categoryRepository
            .deleteById(request.pathVariable(idPathParameterName))
            .flatMap { ok().build() }
}