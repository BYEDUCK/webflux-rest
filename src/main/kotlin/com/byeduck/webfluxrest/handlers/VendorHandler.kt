package com.byeduck.webfluxrest.handlers

import com.byeduck.webfluxrest.domain.Vendor
import com.byeduck.webfluxrest.repositories.VendorRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.body
import reactor.core.publisher.toMono

@Component
class VendorHandler(@Autowired private val vendorRepository: VendorRepository) {

    fun getAll(request: ServerRequest) = ok().body(vendorRepository.findAll())

    fun getById(request: ServerRequest) = ok().body(vendorRepository.findById(request.pathVariable("id")))

    fun add(request: ServerRequest) = ok().body(vendorRepository.saveAll(request.bodyToMono(Vendor::class.java))).toMono()

}