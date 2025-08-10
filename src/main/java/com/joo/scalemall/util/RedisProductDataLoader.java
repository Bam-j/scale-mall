package com.joo.scalemall.util;

import com.joo.scalemall.dto.ProductResponse;
import com.joo.scalemall.service.ProductCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisProductDataLoader implements CommandLineRunner {

    private final ProductCacheService productCacheService;

    @Override
    public void run(String... args) {
        //상황에 따라 stock 변경하기
        ProductResponse productResponse = new ProductResponse(
            1L,
            "[한정 판매] 키링",
            1_000L,
            "글로벌 히트 키링, 한정 판매합니다.",
            99L
        );

        productCacheService
            .saveProductToRedis(productResponse)
            .doOnSuccess(success -> {
                if (Boolean.TRUE.equals(success)) {
                    System.out.println("Data Inserted Successfully");
                } else {
                    System.out.println("Data Insert Failed");
                }
            })
            .then(productCacheService.initStockIfAbsent(productResponse.id(), productResponse.stock()))
            .doOnSuccess(init -> {
                if (Boolean.TRUE.equals(init)) {
                    System.out.println("Stock Key Init Successfully");
                } else {
                    System.out.println("Stock Key Already Exists (skip)");
                }
            })
            .doOnError(error -> {
                System.out.println("Data Insert Failed (Error): " + error.getMessage());
            })
            .subscribe();
    }
}
