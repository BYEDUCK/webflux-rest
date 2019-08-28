package com.byeduck.webfluxrest.routers

import com.byeduck.webfluxrest.constants.baseCategoriesUrl
import com.byeduck.webfluxrest.constants.idPathParameterName
import com.byeduck.webfluxrest.handlers.CategoryHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.router

@Configuration
class CategoryRouter {

    @Bean
    fun categoryRoutes(categoryHandler: CategoryHandler) = router {
        (baseCategoriesUrl and accept(MediaType.APPLICATION_JSON)).nest {
            GET("/", categoryHandler::get)
            GET("/{$idPathParameterName}", categoryHandler::getById)
            POST("/", categoryHandler::add)
            PUT("/{$idPathParameterName}", categoryHandler::updateById)
            DELETE("/{$idPathParameterName}", categoryHandler::deleteById)
        }
    }

}