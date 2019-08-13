package com.byeduck.webfluxrest.bootstrap

import com.byeduck.webfluxrest.domain.Category
import com.byeduck.webfluxrest.domain.Vendor
import com.byeduck.webfluxrest.repositories.CategoryRepository
import com.byeduck.webfluxrest.repositories.VendorRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component

@Component
class Bootstrap(
        @Autowired private val categoryRepository: CategoryRepository,
        @Autowired private val vendorRepository: VendorRepository
) : CommandLineRunner {

    override fun run(vararg args: String?) {
        print("############## LOADING DATA ON BOOTSTRAP ##############")
        fillCategories()
        fillVendors()
    }

    fun fillCategories() {
        if (categoryRepository.count().block()?.equals(0L) == true) {

            categoryRepository.save(Category("Fruits"))
            categoryRepository.save(Category("Nuts"))
            categoryRepository.save(Category("Breads"))
            categoryRepository.save(Category("Meats"))
            categoryRepository.save(Category("Eggs"))

            print("Loaded categories: ${categoryRepository.count().block()}")
        }
    }

    fun fillVendors() {
        if (vendorRepository.count().block()?.equals(0L) == true) {
            vendorRepository.save(Vendor("Joe", "Buck"))
            vendorRepository.save(Vendor("Michael", "Weston"))
            vendorRepository.save(Vendor("Jessie", "Waters"))
            vendorRepository.save(Vendor("Bill", "Nershi"))
            vendorRepository.save(Vendor("Jimmy", "Buffet"))

            print("Loaded vendors: ${vendorRepository.count().block()}")
        }
    }

}