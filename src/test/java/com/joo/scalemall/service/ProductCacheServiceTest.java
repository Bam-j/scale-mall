package com.joo.scalemall.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.joo.scalemall.dto.ProductResponse;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import reactor.test.StepVerifier;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ProductCacheServiceTest {

    @Autowired
    private ProductCacheService productCacheService;
    @Autowired
    private ReactiveStringRedisTemplate reactiveStringRedisTemplate;
    @Autowired
    private ObjectMapper objectMapper;

    private final String key = "product:1";

    private final ProductResponse testProduct = new ProductResponse(
        1L,
        "테스트 상품",
        1_000L,
        "테스트용 상품 설명",
        99L
    );

    @BeforeAll
    void setup() {
        reactiveStringRedisTemplate.delete(key).block();
    }

    @AfterAll
    void teardown() {
        reactiveStringRedisTemplate.delete(key).block();
    }

    @Test
    @DisplayName("상품 정보를 Redis에 저장하고 조회할 수 있다")
    void saveAndGetProduct() {
        StepVerifier.create(productCacheService.saveProductToRedis(testProduct))
            .expectNext(true)
            .verifyComplete();

        StepVerifier.create(productCacheService.getProductFromRedis(1L))
            .assertNext(product -> {
                Assertions.assertEquals(testProduct.id(), product.id());
                Assertions.assertEquals(testProduct.name(), product.name());
                Assertions.assertEquals(testProduct.price(), product.price());
                Assertions.assertEquals(testProduct.description(), product.description());
                Assertions.assertEquals(testProduct.stock(), product.stock());
            })
            .verifyComplete();
    }
}
