package com.example.gateway.apigateway.FilterGateway;

import com.example.gateway.apigateway.Service.RedisService;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;

@Component
public class Filter implements GatewayFilter {

    @Autowired
    RedisService redisService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {


        Mono<WebSession> webSession = exchange.getSession();
        Mono<String> sessionId = webSession.flatMap(session -> {
            return Mono.just(session.getId());
        });
        Object redisValue = redisService.fetch(String.valueOf(sessionId));
        Gson gson = new Gson();
        String newsessionData = gson.toJson(redisValue);
        System.out.println(newsessionData);
        return chain.filter(exchange);
    }



}
