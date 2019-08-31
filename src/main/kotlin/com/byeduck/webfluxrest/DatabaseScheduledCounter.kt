package com.byeduck.webfluxrest

import com.byeduck.webfluxrest.repositories.CategoryRepository
import com.byeduck.webfluxrest.repositories.VendorRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Async
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@EnableAsync
@Component
class DatabaseScheduledCounter(
        @Autowired private val categoryRepository: CategoryRepository,
        @Autowired private val vendorRepository: VendorRepository
) {

    private val logger: Logger = LoggerFactory.getLogger(DatabaseScheduledCounter::class.java)

    @Async
    @Scheduled(fixedDelay = 60000, initialDelay = 15000)
    fun countCategories() = logger.info("Categories count: {}", categoryRepository.count().block())

    @Async
    @Scheduled(fixedDelay = 60000, initialDelay = 5000)
    fun countVendors() = logger.info("Vendors count: {}", vendorRepository.count().block())

}