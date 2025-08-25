package com.joo.scalemall.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.joo.scalemall.dto.ProductResponse;
import com.joo.scalemall.util.enums.DecrementResult;
import com.joo.scalemall.util.enums.PurchaseResult;
import com.joo.scalemall.util.exception.custom.NoStockKeyException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ProductCacheService {

    private final ReactiveStringRedisTemplate reactiveStringRedisTemplate;
    private final ObjectMapper objectMapper;

    //첫 실행 시
    public Mono<Boolean> saveProductToRedis(ProductResponse productResponse) {
        String key = "product:" + productResponse.id();
        try {
            String json = objectMapper.writeValueAsString(productResponse);
            return reactiveStringRedisTemplate.opsForValue().set(key, json);
        } catch (JsonProcessingException e) {
            return Mono.error(e);
        }
    }

    //레디스 DB로부터 상품 정보 조회 및 반환
    public Mono<ProductResponse> getProductFromRedis(Long id) {
        String key = "product:" + id;
        return reactiveStringRedisTemplate.opsForValue()
            .get(key)
            .flatMap(json -> {
                try {
                    ProductResponse productResponse =
                        objectMapper.readValue(json, ProductResponse.class);
                    return Mono.just(productResponse);
                } catch (JsonProcessingException e) {
                    return Mono.error(e);
                }
            });
    }

    //테스트 시작 전 재고 없는 경우 초기화
    public Mono<Boolean> initStockIfAbsent(Long id, Long stock) {
        String stockKey = "stock:product:" + id;
        return reactiveStringRedisTemplate.opsForValue()
            .setIfAbsent(stockKey, String.valueOf(stock));
    }

    //구매 서비스 수행 후 재고 감소
    public Mono<DecrementResult> decrementStockSafely(Long id) {
        String stockKey = "stock:product:" + id;

        String lua = """
                local k=KEYS[1]
                local v=redis.call('GET', k)
                if (not v) then return -1 end
                v=tonumber(v)
                if (v <= 0) then return 0 end
                redis.call('DECR', k)
                return 1
            """;

        RedisScript<Long> script = RedisScript.of(lua, Long.class);

        return reactiveStringRedisTemplate
            .execute(script, List.of(stockKey), List.of())
            .single()
            .map(v -> ((Number) v).intValue())
            .map(code -> {
                switch (code) {
                    case 1:
                        return DecrementResult.SUCCESS;
                    case 0:
                        return DecrementResult.OUT_OF_STOCK;
                    default:
                        return DecrementResult.NO_STOCK_KEY;
                }
            });
    }

    //재고 조회
    public Mono<Long> getStock(Long id) {
        String key = "stock:product:" + id;
        return reactiveStringRedisTemplate.opsForValue()
            .get(key)
            .switchIfEmpty(Mono.error(new NoStockKeyException("재고 키 없음: " + key)))
            .map(Long::parseLong);
    }
}
