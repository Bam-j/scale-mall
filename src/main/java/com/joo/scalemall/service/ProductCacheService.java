package com.joo.scalemall.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.joo.scalemall.dto.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ProductCacheService {

    private final ReactiveRedisTemplate reactiveRedisTemplate;
    private final ObjectMapper objectMapper;

    public Mono<Boolean> saveProductToRedis(ProductResponse productResponse) {
        String key = "product:" + productResponse.id();

        try {
            String json = objectMapper.writeValueAsString(productResponse);
            return reactiveRedisTemplate.opsForValue().set(key, json);
        } catch (JsonProcessingException e) {
            return Mono.error(e);
        }
    }

    public Mono<ProductResponse> getProductFromRedis(Long id) {
        String key = "product:" + id;

        return reactiveRedisTemplate.opsForValue().get(key)
            .flatMap(json -> {
                try {
                    ProductResponse productResponse = objectMapper.readValue(json.toString(), ProductResponse.class);
                    return Mono.just(productResponse);
                } catch (JsonProcessingException e) {
                    return Mono.error(e);
                }
            });
    }

    public Mono<Boolean> initStockIfAbsent(Long id, Long stock) {
        String stockKey = "stock:product" + id;
        return reactiveRedisTemplate.opsForValue().setIfAbsent(stockKey, String.valueOf(stock));
    }
}
