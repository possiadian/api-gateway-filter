package com.example.gateway.apigateway.FilterGateway;

import com.example.gateway.apigateway.Service.RedisService;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;
import reactor.netty.http.server.HttpServerRequest;

import java.net.HttpCookie;
import java.util.Objects;

@Component

public class Filter implements GatewayFilter {

    @Autowired
    RedisService redisService;

    @Autowired
    ReactiveRedisTemplate<String,String> redisTemplate;

    private static final Logger logger = LoggerFactory.getLogger(Filter.class);


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        ServerHttpResponse response = exchange.getResponse();
        String session = String.valueOf(exchange.getRequest().getCookies().getFirst("SESSION"));
        String sessionId = session.substring(session.lastIndexOf("=")+1);
        System.out.println("Session ID: " + sessionId);
        if (sessionId == null) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }
        return redisTemplate.opsForValue().get(sessionId)
                .flatMap(result -> {
                    if (result != null) {
                        logger.info("value found");
                        return chain.filter(exchange);
                    } else {
                        logger.info("value not found");
                        response.setStatusCode(HttpStatus.UNAUTHORIZED);
                        return response.setComplete();
                    }
                });


    }
}
