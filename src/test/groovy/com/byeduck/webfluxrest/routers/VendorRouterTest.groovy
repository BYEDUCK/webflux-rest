package com.byeduck.webfluxrest.routers

import com.byeduck.webfluxrest.constants.StringConstantsKt
import com.byeduck.webfluxrest.domain.Vendor
import com.byeduck.webfluxrest.handlers.VendorHandler
import com.byeduck.webfluxrest.repositories.VendorRepository
import com.byeduck.webfluxrest.validators.VendorValidator
import org.reactivestreams.Publisher
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

    def "Get vendors by last name (valid)"() {
        given:
        def commonLastName = "b"
        def vendorMatch1 = new Vendor("a", commonLastName)
        def vendorMatch2 = new Vendor("c", commonLastName)
        vendorRepository.findByLastName(commonLastName) >> { return Flux.just(vendorMatch1, vendorMatch2) }

        expect:
        webTestClient
                .method(HttpMethod.GET)
                .uri("${baseUrl}?${StringConstantsKt.lastNameQueryParameterName}=${commonLastName}")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Vendor)
                .isEqualTo([vendorMatch1, vendorMatch2])
    }

    def "Get vendors by last name (invalid - blank last name)"() {
        given:
        def invalidLastName = ""

        expect:
        webTestClient
                .method(HttpMethod.GET)
                .uri("${baseUrl}?${StringConstantsKt.lastNameQueryParameterName}=${invalidLastName}")
                .exchange()
                .expectStatus().isNoContent()

        cleanup:
        0 * vendorRepository.findByLastName(_ as String)
    }

    def "Get vendor by id (match) test"() {
        given: "vendor with id"
        def vendor = new Vendor("a", "b")
        vendor.id = "123"
        vendorRepository.findById(vendor.id) >> { return Mono.just(vendor) }

        expect:
        webTestClient
                .method(HttpMethod.GET)
                .uri("${baseUrl}/${vendor.id}")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Vendor)
                .isEqualTo(vendor)
    }

    def "Get vendor by id (no match)"() {
        given:
        def vendorId = "123"
        vendorRepository.findById(vendorId) >> { return Mono.empty() }

        expect:
        webTestClient
                .method(HttpMethod.GET)
                .uri("${baseUrl}/${vendorId}")
                .exchange()
                .expectStatus().isNoContent()
    }

    def "Add vendor (good) test"() {
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

    def "Add vendor (invalid - with blank last name)"() {
        given:
        def vendor = new Vendor()
        vendor.firstName = "a"

        expect:
        webTestClient
                .method(HttpMethod.POST)
                .uri(baseUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromObject(vendor))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(String)
                .isEqualTo(StringConstantsKt.blankVendorLastNameMsg)

        cleanup:
        0 * vendorRepository.saveAll(_ as Publisher)
    }

    def "Update vendor by id (good)"() {
        given:
        def id = "123"
        def vendor = new Vendor("a", "b")
        def savedVendor = new Vendor("c", "d")
        savedVendor.id = id
        def updatedVendor = new Vendor(vendor.firstName, vendor.lastName)
        updatedVendor.id = id
        vendorRepository.findById(id) >> { return Mono.just(savedVendor) }
        vendorRepository.saveAll(_ as Mono) >> { return Flux.just(updatedVendor) }

        expect:
        webTestClient
                .method(HttpMethod.PUT)
                .uri("${baseUrl}/${id}")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromObject(vendor))
                .exchange()
                .expectStatus().isOk()
                .expectBody(Vendor)
                .isEqualTo(updatedVendor)

    }

    def "Update vendor by id (not found)"() {
        given:
        def id = "123"
        def vendor = new Vendor("a", "b")
        vendorRepository.findById(id) >> { return Mono.empty() }

        expect:
        webTestClient
                .method(HttpMethod.PUT)
                .uri("${baseUrl}/${id}")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromObject(vendor))
                .exchange()
                .expectStatus().isNotFound()

        cleanup:
        0 * vendorRepository.saveAll(_ as Publisher)
    }

    def "Update vendor by id (invalid vendor)"() {
        given:
        def id = "123"
        def vendor = new Vendor()
        vendor.firstName = "a"

        expect:
        webTestClient
                .method(HttpMethod.PUT)
                .uri("${baseUrl}/${id}")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromObject(vendor))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(String)
                .isEqualTo(StringConstantsKt.blankVendorLastNameMsg)

        cleanup:
        0 * vendorRepository.findById(_ as String)
    }
}
