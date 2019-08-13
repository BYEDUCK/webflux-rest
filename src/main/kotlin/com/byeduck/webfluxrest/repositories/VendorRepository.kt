package com.byeduck.webfluxrest.repositories

import com.byeduck.webfluxrest.domain.Vendor
import org.springframework.data.mongodb.repository.ReactiveMongoRepository

interface VendorRepository : ReactiveMongoRepository<Vendor, String>