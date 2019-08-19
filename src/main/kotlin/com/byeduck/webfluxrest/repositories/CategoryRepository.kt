package com.byeduck.webfluxrest.repositories

import com.byeduck.webfluxrest.domain.Category
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Mono

interface CategoryRepository : ReactiveMongoRepository<Category, String> {
    fun findByDescription(description: String): Mono<Category>
}