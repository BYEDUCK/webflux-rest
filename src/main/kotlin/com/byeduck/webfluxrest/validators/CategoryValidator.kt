package com.byeduck.webfluxrest.validators

import com.byeduck.webfluxrest.domain.Category
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class CategoryValidator : Validator<Category> {
    override fun validate(toBeValidated: Mono<Category>): Mono<Category> =
            toBeValidated.filter { c -> c.description.isNotBlank() }
}