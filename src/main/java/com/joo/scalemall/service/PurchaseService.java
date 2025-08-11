package com.joo.scalemall.service;

import com.joo.scalemall.dto.PurchaseRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class PurchaseService {

    private final ProductCacheService productCacheService;

    public Mono<ResponseEntity<String>> purchase(PurchaseRequest purchaseRequest) {
        Long id = purchaseRequest.id();

        return productCacheService.decrementStockSafely(id)
            .map(result -> {
                switch (result) {
                    case SUCCESS:
                        return ResponseEntity.ok("구매 성공");
                    case OUT_OF_STOCK:
                        return ResponseEntity.status(409).body("품절");
                    case NO_STOCK_KEY:
                    default:
                        return ResponseEntity.status(500).body("재고 키 없음");
                }
            });
    }
}
