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
        println("############## LOADING DATA ON BOOTSTRAP ##############")
        fillCategories()
        fillVendors()
    }

    fun fillCategories() {
        if (categoryRepository.count().block()?.equals(0L) == true) {

            saveCategory("Fruits")
            saveCategory("Nuts")
            saveCategory("Breads")
            saveCategory("Meats")
            saveCategory("Eggs")

            println("Loaded categories: ${categoryRepository.count().block()}")
        } else {
            println("Categories already added. Skipping...")
        }
    }

    fun fillVendors() {
        if (vendorRepository.count().block()?.equals(0L) == true) {

            saveVendor("Joe", "Buck")
            saveVendor("Michael", "Weston")
            saveVendor("Jessie", "Waters")
            saveVendor("Bill", "Nershi")
            saveVendor("Jimmy", "Buffet")

            println("Loaded vendors: ${vendorRepository.count().block()}")
        } else {
            println("Vendors already added. Skipping...")
        }
    }

    fun saveCategory(description: String) {
        val saved = categoryRepository.save(Category(description)).block()
        println("Saved category with id ${saved?.id}")
    }

    fun saveVendor(firstName: String, lastName: String) {
        val saved = vendorRepository.save(Vendor(firstName, lastName)).block()
        println("Saved vendor with id ${saved?.id}")
    }

}