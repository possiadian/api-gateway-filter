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

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;


@Component
    public class NormalFilter implements GatewayFilter {

        @Autowired
        RedisTemplate<String,String> redisTemplate;

        @Override
        public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

             final Logger logger = LoggerFactory.getLogger(NormalFilter.class);


            ServerHttpResponse response = exchange.getResponse();
            String session = String.valueOf(exchange.getRequest().getCookies().getFirst("SESSION"));
            String sessionIdtrim = session.substring(session.lastIndexOf("=")+1);
            byte[] sessiondecoded= Base64.getDecoder().decode(sessionIdtrim);
            String sessionId = new String(sessiondecoded, StandardCharsets.UTF_8);
            String key = "spring:session:sessions:" + sessionId;
            System.out.println(key);

            System.out.println("Session ID: " + sessionId);
            if (sessionId == null) {
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                return response.setComplete();
            }

           boolean keyExists = redisTemplate.hasKey(key);
            if(keyExists){
                logger.info("key exists");
                return chain.filter(exchange);
            }else{
                logger.info("Value not found");
                String redirectUri = "http://localhost:8082/login";  // Replace with your desired redirect URI
                exchange.getResponse().setStatusCode(HttpStatus.valueOf(302));  // Temporary redirect
                exchange.getResponse().getHeaders().setLocation(URI.create(redirectUri));
                return Mono.empty();

            }

        }


    }



