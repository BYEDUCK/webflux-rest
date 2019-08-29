package com.byeduck.webfluxrest.repositories

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import spock.lang.Specification

@DataMongoTest
class CategoryRepositoryIT extends Specification {

    @Autowired
    CategoryRepository categoryRepository

    def "Without any action - repository is empty"() {
        expect:
        categoryRepository.count().block() == 0
    }

    def "Simple insert one element test"() {
        when: "adding an element to an empty repository"
        categoryRepository.save(new Category("test5")).block()

        then: "the count should be equal to 1"
        categoryRepository.count().block() == 1
    }

    def "Retrieve saved element by id test"() {
        given: "a repository with one element"
        def testDescription = "test1"
        def saved = categoryRepository.save(new Category(testDescription)).block()

        when: "retrieving this element"
        def retrieved = categoryRepository.findById(saved.id).block()

        then: "descriptions of saved and retrieved should be equal and id should not be empty"
        retrieved.description == testDescription
        !retrieved.id.isEmpty()
    }

    def "Retrieve saved element by description test"() {
        given: "a repository with one element with given description"
        def testDescription = "testDescription"
        def saved = categoryRepository.save(new Category(testDescription)).block()

        when: "retrieving the element by its description"
        def retrieved = categoryRepository.findByDescription(saved.description).block()

        then: "descriptions of saved and retrieved should be equal and id should not be empty"
        retrieved.description == testDescription
        !retrieved.id.isEmpty()
    }

    def "Update saved element test"() {
        given: "saved element with given description"
        def saved = categoryRepository.save(new Category("test123")).block()
        def newDescription = "test12345"

        when: "updating this element"
        saved.description = newDescription
        categoryRepository.save(saved).block()
        def retrievedUpdated = categoryRepository.findById(saved.id).block()

        then: "it should have the same id and updated description"
        retrievedUpdated.id == saved.id
        retrievedUpdated.description == newDescription
    }

    def "Remove saved element test"() {
        given: "a repository with one saved element"
        def saved = categoryRepository.save(new Category("toDelete")).block()

        when: "deleting this element"
        categoryRepository.delete(saved).block()

        then: "repository should no longer contain it"
        !categoryRepository.findById(saved.id).blockOptional().isPresent()
    }
}
