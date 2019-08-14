package com.byeduck.webfluxrest.routers

import com.byeduck.webfluxrest.handlers.CategoryHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.router

@Configuration
class CategoryRouter {

    @Bean
    fun categoryRoutes(categoryHandler: CategoryHandler) = router {
        ("/api/v1/categories" and accept(MediaType.APPLICATION_JSON)).nest {
            GET("/", categoryHandler::getAll)
            GET("/{id}", categoryHandler::getById)
            POST("/", categoryHandler::add)
        }
    }

}