package com.example.gateway.apigateway.FilterGateway;

import com.example.gateway.apigateway.Service.RedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;





    @Component
    public class NormalFilter implements GatewayFilter {

        @Autowired
        RedisTemplate<String,String> redisTemplate;

        @Override
        public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

             final Logger logger = LoggerFactory.getLogger(NormalFilter.class);


            ServerHttpResponse response = exchange.getResponse();
            String session = String.valueOf(exchange.getRequest().getCookies().getFirst("SESSION"));
            String sessionId = session.substring(session.lastIndexOf("=")+1);
            System.out.println("Session ID: " + sessionId);
            if (sessionId == null) {
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                return response.setComplete();
            }
            if (redisTemplate.opsForValue().get(sessionId) != null) {
                logger.info("Value found");
                return chain.filter(exchange);
            } else {
                logger.info("Value Not found" );
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                return response.setComplete();
            }
        }


    }



