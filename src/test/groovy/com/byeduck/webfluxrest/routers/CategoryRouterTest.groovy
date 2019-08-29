package com.byeduck.webfluxrest.routers

import com.byeduck.webfluxrest.constants.StringConstantsKt
import com.byeduck.webfluxrest.handlers.CategoryHandler
import com.byeduck.webfluxrest.repositories.CategoryRepository
import com.byeduck.webfluxrest.validators.CategoryValidator
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.BodyInserters
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import spock.lang.Shared
import spock.lang.Specification

class CategoryRouterTest extends Specification {

    @Shared
    WebTestClient webTestClient

    def categoryRepository = Mock(CategoryRepository)

    static def baseUrl = "/api/v1/categories"

    void setup() {
        def categoryRouter = new CategoryRouter()
        def routerFunction = categoryRouter.categoryRoutes(new CategoryHandler(categoryRepository, new CategoryValidator()))

        webTestClient = WebTestClient.bindToRouterFunction(routerFunction).build()
    }

    def "Get all categories test"() {
        given:
        def categoryList = [new Category("test1"), new Category("test2")]
        categoryRepository.findAll() >> { return Flux.just(categoryList.toArray()) }

        expect:
        webTestClient
                .method(HttpMethod.GET)
                .uri(baseUrl)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Category)
                .isEqualTo(categoryList)
    }

    def "Get category by id (match)"() {
        given:
        def id = "testId"
        def category = new Category("test", id)
        categoryRepository.findById(id) >> { return Mono.just(category) }

        expect:
        webTestClient
                .method(HttpMethod.GET)
                .uri("${baseUrl}/${id}")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Category)
                .isEqualTo(category)
    }

    def "Get category by id (no match)"() {
        given:
        def id = "testId"
        categoryRepository.findById(id) >> { return Mono.empty() }

        expect:
        webTestClient
                .method(HttpMethod.GET)
                .uri("${baseUrl}/${id}")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNoContent()
    }

    def "Insert category (good)"() {
        given:
        def desc = "testDesc"
        def id = "testId"
        def category = new Category(desc)
        def savedCategory = new Category(desc, id)
        categoryRepository.saveAll(_ as Mono) >> { return Flux.just(savedCategory) }
        categoryRepository.findByDescription(desc) >> { return Mono.empty() }

        expect:
        webTestClient
                .method(HttpMethod.POST)
                .uri(baseUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromObject(category))
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().valueEquals(HttpHeaders.LOCATION, "${baseUrl}/${id}")
                .expectBody(Category)
                .isEqualTo(savedCategory)
    }

    def "Insert category (invalid)"() {
        given:
        def category = new Category("")

        expect:
        webTestClient
                .method(HttpMethod.POST)
                .uri(baseUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromObject(category))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(String)
                .isEqualTo(StringConstantsKt.blankCategoryDescriptionMsg)
    }

    def "Insert category (category with given description already exists)"() {
        given:
        def desc = "test"
        def category = new Category(desc)
        categoryRepository.findByDescription(desc) >> { return Mono.just(new Category(desc, "testId")) }

        expect:
        webTestClient
                .method(HttpMethod.POST)
                .uri(baseUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromObject(category))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(String)
                .isEqualTo(StringConstantsKt.givenCategoryAlreadyExistsMsg)
    }

    def "Update category (good)"() {
        given:
        def id = "testId"
        def desc = "testDesc"
        def updateDesc = "newDesc"
        def category = new Category(desc, id)
        def updateCategory = new Category(updateDesc)
        categoryRepository.findById(id) >> { return Mono.just(category) }
        categoryRepository.findByDescription(updateDesc) >> { return Mono.empty() }
        categoryRepository.saveAll(_ as Mono) >> { return Flux.just(new Category(updateCategory.description, id)) }

        expect:
        webTestClient
                .method(HttpMethod.PUT)
                .uri("${baseUrl}/${id}")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromObject(updateCategory))
                .exchange()
                .expectStatus().isOk()
                .expectBody(Category)
                .isEqualTo(new Category(updateCategory.description, id))
    }

    def "Update category with description that already exists"() {
        given:
        def id = "testId"
        def desc = "testDesc"
        def updateDesc = "newDesc"
        def category = new Category(desc, id)
        def updateCategory = new Category(updateDesc)
        categoryRepository.findById(id) >> { return Mono.just(category) }
        categoryRepository.findByDescription(updateDesc) >> { return Mono.just(new Category(updateDesc, "123")) }

        expect:
        webTestClient
                .method(HttpMethod.PUT)
                .uri("${baseUrl}/${id}")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromObject(updateCategory))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(String)
                .isEqualTo(StringConstantsKt.givenCategoryAlreadyExistsMsg)
    }

    def "Update category (invalid)"() {
        given:
        def id = "testId"
        def category = new Category("")
        categoryRepository.findById(id) >> { return Mono.just(new Category("testDesc", id)) }

        expect:
        webTestClient
                .method(HttpMethod.PUT)
                .uri("${baseUrl}/${id}")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromObject(category))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(String)
                .isEqualTo(StringConstantsKt.blankCategoryDescriptionMsg)
    }

    def "Update category (not found)"() {
        given:
        def id = "testId"
        def category = new Category("testDesc")
        categoryRepository.findById(id) >> { return Mono.empty() }

        expect:
        webTestClient
                .method(HttpMethod.PUT)
                .uri("${baseUrl}/${id}")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromObject(category))
                .exchange()
                .expectStatus().isNotFound()
    }

    def "Get category by description (good)"() {
        given:
        def desc = "testDesc"
        def id = "testId"
        def foundCategory = new Category(desc, id)
        categoryRepository.findByDescription(desc) >> { return Mono.just(foundCategory) }

        expect:
        webTestClient
                .method(HttpMethod.GET)
                .uri("${baseUrl}?${StringConstantsKt.descriptionQueryParameterName}=${desc}")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Category)
                .isEqualTo(foundCategory)
    }

    def "Get category by description (not found)"() {
        given:
        def desc = "testDesc"
        categoryRepository.findByDescription(desc) >> { return Mono.empty() }

        expect:
        webTestClient
                .method(HttpMethod.GET)
                .uri("${baseUrl}?${StringConstantsKt.descriptionQueryParameterName}=${desc}")
                .exchange()
                .expectStatus().isNotFound()
    }

    def "Get category by description (blank description)"() {
        expect:
        webTestClient
                .method(HttpMethod.GET)
                .uri("${baseUrl}?${StringConstantsKt.descriptionQueryParameterName}=")
                .exchange()
                .expectStatus().isNoContent()
    }
}
