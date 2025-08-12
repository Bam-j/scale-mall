package com.joo.scalemall.controller;

import com.joo.scalemall.dto.PurchaseRequest;
import com.joo.scalemall.service.ProductCacheService;
import com.joo.scalemall.service.ProductService;
import com.joo.scalemall.service.PurchaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final PurchaseService purchaseService;
    private final ProductService productService;

    @GetMapping("/{id}/stock")
    public Mono<ResponseEntity<String>> getStock(@PathVariable Long id){
        return productService.getStock(id);
    }

    @PostMapping("/purchase")
    public Mono<ResponseEntity<String>> purchase(@RequestBody PurchaseRequest purchaseRequest) {
        return purchaseService.purchase(purchaseRequest);
    }
}
