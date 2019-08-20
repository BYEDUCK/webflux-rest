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

    constructor(description: String, id: String) : this() {
        this.description = description
        this.id = id
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Category) return false

        if (id != other.id) return false
        if (description != other.description) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + description.hashCode()
        return result
    }


}