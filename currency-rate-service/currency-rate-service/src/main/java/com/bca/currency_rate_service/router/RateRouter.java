package com.bca.currency_rate_service.router;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;

import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.bca.currency_rate_service.handler.RateHandler;

@Configuration
public class RateRouter {

    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = "/api/v1/rates",
                    produces = "application/json",
                    method = RequestMethod.GET,
                    beanClass = RateHandler.class,
                    beanMethod = "getRate"
            )
    })
    public RouterFunction<ServerResponse> rateRoutes(RateHandler handler) {

        return RouterFunctions.route(GET("/api/v1/rates"), handler::getRate);
    }
}