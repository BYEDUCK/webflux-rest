package com.byeduck.webfluxrest.routers

import com.byeduck.webfluxrest.handlers.VendorHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.router

@Configuration
class VendorRouter {

    @Bean
    fun vendorRoutes(vendorHandler: VendorHandler) = router {
        ("/api/v1/vendors" and accept(MediaType.APPLICATION_JSON)).nest {
            GET("/", vendorHandler::getAll)
            GET("/{id}", vendorHandler::getById)
            POST("/", vendorHandler::add)
        }
    }

}