package com.byeduck.webfluxrest.handlers

import com.byeduck.webfluxrest.constants.baseVendorsUrl
import com.byeduck.webfluxrest.constants.blankVendorLastNameMsg
import com.byeduck.webfluxrest.constants.idPathParameterName
import com.byeduck.webfluxrest.constants.lastNameQueryParameterName
import com.byeduck.webfluxrest.domain.Vendor
import com.byeduck.webfluxrest.repositories.VendorRepository
import com.byeduck.webfluxrest.validators.VendorValidator
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
class VendorHandler(
        @Autowired private val vendorRepository: VendorRepository,
        @Autowired private val vendorValidator: VendorValidator
) {

    fun getAll(request: ServerRequest): Mono<ServerResponse> = request.queryParam(lastNameQueryParameterName)
            .map { lastName ->
                Optional.of(lastName).filter { it.isNotBlank() }.map {
                    ok().body(vendorRepository.findByLastName(lastName))
                }.orElse(noContent().build())
            }.orElse(Mono.defer { ok().body(vendorRepository.findAll()) })

    fun getById(request: ServerRequest): Mono<ServerResponse> = vendorRepository
            .findById(request.pathVariable(idPathParameterName))
            .flatMap { ok().body(it.toMono()) }
            .switchIfEmpty(noContent().build())

    fun add(request: ServerRequest): Mono<ServerResponse> = vendorValidator
            .validate(request.bodyToMono(Vendor::class.java))
            .flatMap { vendorRepository.saveAll(it.toMono()).toMono() }
            .flatMap { created(URI.create("$baseVendorsUrl/${it.id}")).body(it.toMono()) }
            .switchIfEmpty(badRequest().body(Mono.just(blankVendorLastNameMsg)))

    fun updateById(request: ServerRequest): Mono<ServerResponse> = vendorValidator
            .validate(request.bodyToMono(Vendor::class.java))
            .flatMap {
                vendorRepository.findById(request.pathVariable(idPathParameterName))
                        .flatMap { Mono.defer { ok().body(vendorRepository.saveAll(it.toMono()).toMono()) } }
                        .switchIfEmpty(notFound().build())
            }.switchIfEmpty(badRequest().body(Mono.just(blankVendorLastNameMsg)))

    fun deleteById(request: ServerRequest): Mono<ServerResponse> = ok()
            .body(vendorRepository.deleteById(request.pathVariable(idPathParameterName)))

}