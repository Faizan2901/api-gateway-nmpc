package com.codemind.playcenter.apigatewayplaycenter.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.codemind.playcenter.apigatewayplaycenter.config.ApplicationProperties;



@Configuration
public class ApiConfiguration {
	
	@Autowired
	ApplicationProperties applicationProperties;
	
    @Bean
    public RouteLocator gatewayRouter(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(route -> route.path("/authentication-service/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://authentication-service"))
                .route(route -> route.path("/registration-service/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://registration-service"))
                .route(route -> route.path("/user-service/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://user-service"))
                .route(route -> route.path("/dashboard-service/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://dashboard-service"))
                .build();
    }
    
}
