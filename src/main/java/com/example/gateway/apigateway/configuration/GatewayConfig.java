package com.example.gateway.apigateway.configuration;

import com.example.gateway.apigateway.FilterGateway.Filter;
import com.example.gateway.apigateway.FilterGateway.NormalFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {
  @Autowired
  Filter filter;
  @Autowired
  NormalFilter normalFilter;


    @Bean
    public RouteLocator customRouteBuilder(RouteLocatorBuilder builder){

        return builder.routes()
                .route(r-> r.path("/login")
                        .uri("http://localhost:8081/"))
                .route(r -> r.path("/healthcheck")
                        .filters(f-> f.filter(normalFilter))
                        .uri("http://localhost:8084/"))
                .build();


    }
}
