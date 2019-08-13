package com.byeduck.webfluxrest.domain

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document
class Vendor() {
    @Id
    var id: String? = null

    var firstName: String = ""
    var lastName: String = ""

    constructor(firstName: String, lastName: String) : this() {
        this.firstName = firstName
        this.lastName = lastName
    }
}