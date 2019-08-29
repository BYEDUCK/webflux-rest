package com.byeduck.webfluxrest.routers

import com.byeduck.webfluxrest.constants.baseVendorsUrl
import com.byeduck.webfluxrest.constants.idPathParameterName
import com.byeduck.webfluxrest.handlers.VendorHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.router

@Configuration
class VendorRouter {

    @Bean
    fun vendorRoutes(vendorHandler: VendorHandler) = router {
        (baseVendorsUrl and accept(MediaType.APPLICATION_JSON)).nest {
            GET("/", vendorHandler::getAll)
            GET("/{$idPathParameterName}", vendorHandler::getById)
            POST("/", vendorHandler::add)
            PUT("/{$idPathParameterName}", vendorHandler::updateById)
            DELETE("/{$idPathParameterName}", vendorHandler::deleteById)
        }
    }

}