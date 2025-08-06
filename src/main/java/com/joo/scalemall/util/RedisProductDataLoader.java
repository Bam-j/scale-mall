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
        ProductResponse productResponse = new ProductResponse(
            1L,
            "[한정 판매] 키링",
            1_000L,
            "글로벌 히트 키링, 한정 판매합니다.",
            99L    //99L, 999L, 9999L, ...
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
            .doOnError(error -> {
                System.out.println("Data Insert Failed (Error): " + error.getMessage());
            })
            .subscribe();
    }
}
