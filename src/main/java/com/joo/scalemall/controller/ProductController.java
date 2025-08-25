package com.joo.scalemall.controller;

import com.joo.scalemall.dto.ApiResponse;
import com.joo.scalemall.dto.PurchasePayload;
import com.joo.scalemall.dto.PurchaseRequest;
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
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final PurchaseService purchaseService;
    private final ProductService productService;

    //상세 페이지에서 잔여 재고 요청
    @GetMapping("/{id}/stock")
    public Mono<ResponseEntity<String>> getStock(@PathVariable Long id) {
        return productService.getStock(id);
    }

    //상세 페이지에서 구매 버튼 클릭을 통한 구매 요청
    @PostMapping("/purchase")
    public Mono<ResponseEntity<ApiResponse<PurchasePayload>>> purchase(
        @RequestBody PurchaseRequest purchaseRequest,
        ServerWebExchange exchange
    ) {
        return purchaseService.purchase(purchaseRequest, exchange);
    }
}
