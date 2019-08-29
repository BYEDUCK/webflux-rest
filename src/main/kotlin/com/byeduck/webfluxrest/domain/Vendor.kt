package com.byeduck.webfluxrest.domain

import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document
class Vendor() {
    @Id
    var id: String? = null

    @JsonProperty("first_name")
    var firstName: String = ""

    @JsonProperty("last_name")
    var lastName: String = ""

    constructor(firstName: String, lastName: String) : this() {
        this.firstName = firstName
        this.lastName = lastName
    }

    constructor(id: String, firstName: String, lastName: String) : this() {
        this.id = id
        this.firstName = firstName
        this.lastName = lastName
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Vendor) return false

        if (id != other.id) return false
        if (firstName != other.firstName) return false
        if (lastName != other.lastName) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + firstName.hashCode()
        result = 31 * result + lastName.hashCode()
        return result
    }

    override fun toString(): String {
        return "Vendor(id=$id, firstName='$firstName', lastName='$lastName')"
    }


}