package com.joo.scalemall.service;

import com.joo.scalemall.dto.StockResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductCacheService productCacheService;

    public Mono<ResponseEntity<String>> getStock(Long id) {
        return productCacheService.getStock(id)
            .map(stock -> ResponseEntity.ok(stock.toString()))
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
