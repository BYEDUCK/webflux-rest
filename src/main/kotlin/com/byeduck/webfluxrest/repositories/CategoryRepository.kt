package com.byeduck.webfluxrest.repositories

import com.byeduck.webfluxrest.domain.Category
import org.springframework.data.mongodb.repository.ReactiveMongoRepository

interface CategoryRepository : ReactiveMongoRepository<Category, String>