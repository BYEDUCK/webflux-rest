package com.byeduck.webfluxrest.validators

import com.byeduck.webfluxrest.domain.Vendor
import reactor.core.publisher.Mono
import spock.lang.Specification
import spock.lang.Unroll

class VendorValidatorTest extends Specification {
    def validator = new VendorValidator()

    static def validVendors = [new Vendor("fistName", "lastName"), new Vendor("", "lastName")]
    static def invalidVendors = [new Vendor("firstName", ""), new Vendor()]

    @Unroll
    def "Validating valid vendors should return themselves"() {
        expect:
        validator.validate(Mono.just(v)).block() == v

        where:
        v << validVendors
    }

    @Unroll
    def "Validating invalid vendors should return empty mono"() {
        expect:
        !validator.validate(Mono.just(v)).blockOptional().isPresent()

        where:
        v << invalidVendors
    }
}
