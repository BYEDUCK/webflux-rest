package com.byeduck.webfluxrest.handlers

import com.byeduck.webfluxrest.constants.baseVendorsUrl
import com.byeduck.webfluxrest.constants.blankVendorLastNameMsg
import com.byeduck.webfluxrest.constants.idPathParameterName
import com.byeduck.webfluxrest.domain.Vendor
import com.byeduck.webfluxrest.repositories.VendorRepository
import com.byeduck.webfluxrest.validators.VendorValidator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse.*
import org.springframework.web.reactive.function.server.body
import reactor.core.publisher.Mono
import reactor.core.publisher.toMono
import java.net.URI

@Component
class VendorHandler(
        @Autowired private val vendorRepository: VendorRepository,
        @Autowired private val vendorValidator: VendorValidator
) {

    fun getAll(request: ServerRequest) = ok()
            .body(vendorRepository.findAll())

    fun getById(request: ServerRequest) = ok()
            .body(vendorRepository.findById(request.pathVariable(idPathParameterName)))

    fun add(request: ServerRequest) = vendorRepository
            .saveAll(vendorValidator.validate(request.bodyToMono(Vendor::class.java)))
            .flatMap { created(URI.create("$baseVendorsUrl/${it.id}")).body(it.toMono()) }
            .switchIfEmpty(badRequest().body(Mono.just(blankVendorLastNameMsg))).toMono()

}