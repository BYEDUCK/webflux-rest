package com.byeduck.webfluxrest.routers

import com.byeduck.webfluxrest.handlers.CategoryHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.RequestPredicate
import org.springframework.web.reactive.function.server.RequestPredicates
import org.springframework.web.reactive.function.server.router

@Configuration
class CategoryRouter {

    @Bean
    fun categoryRoutes(categoryHandler: CategoryHandler) = router {
        ("/api/v1/categories" and accept(MediaType.APPLICATION_JSON)).nest {
            GET("/", categoryHandler::get)
            GET("/{id}", categoryHandler::getById)
            GET("/", categoryHandler::getByDescription)
            POST("/", categoryHandler::add)
            PUT("/{id}", categoryHandler::updateById)
            DELETE("/{id}", categoryHandler::deleteById)
        }
    }

    @Bean
    fun categoryRoutesWithDescriptionQueryParam(categoryHandler: CategoryHandler) = router {
        ("/api/v1/categories" and hasDescQueryParam()).nest {
            GET("/", categoryHandler::getByDescription)
        }
    }

    fun hasDescQueryParam(): RequestPredicate = RequestPredicates.queryParam("desc") { it.isNotEmpty() }

}