package com.byeduck.webfluxrest.validators

import reactor.core.publisher.Mono

interface Validator<T> {
    fun validate(toBeValidated: Mono<T>): Mono<Boolean>
}