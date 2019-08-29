package com.byeduck.webfluxrest.routers

import com.byeduck.webfluxrest.domain.Vendor
import com.byeduck.webfluxrest.handlers.VendorHandler
import com.byeduck.webfluxrest.repositories.VendorRepository
import com.byeduck.webfluxrest.validators.VendorValidator
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.BodyInserters
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import spock.lang.Shared
import spock.lang.Specification

class VendorRouterTest extends Specification {

    @Shared
    WebTestClient webTestClient

    def vendorRepository = Mock(VendorRepository)

    static def baseUrl = "/api/v1/vendors"

    void setup() {
        def vendorRouter = new VendorRouter()
        def routerFunction = vendorRouter.vendorRoutes(new VendorHandler(vendorRepository, new VendorValidator()))
        webTestClient = WebTestClient.bindToRouterFunction(routerFunction).build()
    }

    def "Get all vendors test"() {
        given: "two vendors to return"
        def vendorList = [new Vendor("a", "b"), new Vendor("c", "d")]
        vendorRepository.findAll() >> { return Flux.just(vendorList.toArray()) }

        expect:
        webTestClient
                .method(HttpMethod.GET)
                .uri(baseUrl)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Vendor)
                .isEqualTo(vendorList)
    }

    def "Get vendor by id test"() {
        given: "vendor with id"
        def vendor = new Vendor("a", "b")
        vendor.id = "123"
        vendorRepository.findById(vendor.id) >> { return Mono.just(vendor) }

        expect:
        webTestClient
                .method(HttpMethod.GET)
                .uri("${baseUrl}/{id}", vendor.id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Vendor)
                .isEqualTo(vendor)
    }

    def "Add vendor test"() {
        given: "vendor without id ready to be saved"
        def vendor = new Vendor("a", "b")
        def saved = new Vendor(vendor.firstName, vendor.lastName)
        vendorRepository.saveAll(_ as Mono) >> {
            saved.id = "1"
            return Flux.just(saved)
        }

        expect:
        webTestClient
                .method(HttpMethod.POST)
                .uri(baseUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromObject(vendor))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Vendor)
                .isEqualTo(saved)
    }
}
