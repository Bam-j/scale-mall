package com.joo.scalemall.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductCacheService productCacheService;

    //재고 로드 서비스를 호출하는 서비스
    public Mono<ResponseEntity<String>> getStock(Long id) {
        return productCacheService.getStock(id)
            .map(stock -> ResponseEntity.ok(stock.toString()))
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
