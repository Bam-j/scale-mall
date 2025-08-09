package com.joo.scalemall.controller;

import com.joo.scalemall.dto.PurchaseRequest;
import com.joo.scalemall.service.PurchaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final PurchaseService purchaseService;

    @PostMapping("/purchase")
    public ResponseEntity<String> purchase(@RequestBody PurchaseRequest purchaseRequest) {
        purchaseService.purchase(purchaseRequest);
        return ResponseEntity.ok("구매 성공");
    }
}
