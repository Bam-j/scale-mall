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

    public Mono<Boolean> saveProductToRedis(ProductResponse productResponse) {
        String key = "product:" + productResponse.id();
        try {
            String json = objectMapper.writeValueAsString(productResponse);
            return reactiveStringRedisTemplate.opsForValue().set(key, json);
        } catch (JsonProcessingException e) {
            return Mono.error(e);
        }
    }

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

    public Mono<Boolean> initStockIfAbsent(Long id, Long stock) {
        String stockKey = "stock:product:" + id;
        return reactiveStringRedisTemplate.opsForValue()
            .setIfAbsent(stockKey, String.valueOf(stock));
    }

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

    public Mono<Long> getStock(Long id) {
        String key = "stock:product:" + id;
        return reactiveStringRedisTemplate.opsForValue()
            .get(key)
            .switchIfEmpty(Mono.error(new NoStockKeyException("재고 키 없음: " + key)))
            .map(Long::parseLong);
    }


    public Mono<PurchaseResult> purchaseOnce(Long productId, String clientId) {
        String stockKey = "stock:product:" + productId;
        String purchasedKey = "purchased:product:" + productId;

        String lua = """
                local stockKey     = KEYS[1]
                local purchasedKey = KEYS[2]
                local cid          = ARGV[1]
            
                if (redis.call('SISMEMBER', purchasedKey, cid) == 1) then
                  return -2 -- ALREADY_PURCHASED
                end
            
                local v = redis.call('GET', stockKey)
                if (not v) then
                  return -1 -- NO_STOCK_KEY
                end
                v = tonumber(v)
                if (v <= 0) then
                  return 0  -- OUT_OF_STOCK
                end
            
                redis.call('DECR', stockKey)
                redis.call('SADD', purchasedKey, cid)
            
                return 1    -- SUCCESS
            """;

        RedisScript<Long> script = RedisScript.of(lua, Long.class);

        return reactiveStringRedisTemplate
            .execute(script, List.of(stockKey, purchasedKey), List.of(clientId))
            .single()
            .map(code -> {
                int c = code.intValue();
                switch (c) {
                    case 1:
                        return PurchaseResult.SUCCESS;
                    case 0:
                        return PurchaseResult.OUT_OF_STOCK;
                    case -1:
                        return PurchaseResult.NO_STOCK_KEY;
                    case -2:
                        return PurchaseResult.ALREADY_PURCHASED;
                    default:
                        return PurchaseResult.NO_STOCK_KEY;
                }
            });
    }
}
