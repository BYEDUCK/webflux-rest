package com.byeduck.webfluxrest.repositories

import com.byeduck.webfluxrest.domain.Vendor
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Flux

interface VendorRepository : ReactiveMongoRepository<Vendor, String> {

    fun findByLastName(lastName: String): Flux<Vendor>

}