package com.byeduck.webfluxrest.validators

import com.byeduck.webfluxrest.domain.Vendor
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class VendorValidator : Validator<Vendor> {
    override fun validate(toBeValidated: Mono<Vendor>): Mono<Vendor> =
            toBeValidated.filter { v -> v.lastName.isNotBlank() }
}