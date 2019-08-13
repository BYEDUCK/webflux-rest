package com.byeduck.webfluxrest.domain

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document
class Category() {
    @Id
    var id: String? = null

    var description: String = ""

    constructor(description: String) : this() {
        this.description = description
    }
}