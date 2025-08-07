package com.joo.scalemall.controller;

import com.joo.scalemall.dto.PurchaseRequest;
import com.joo.scalemall.service.PurchaseSystem;
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

    private final PurchaseSystem purchaseSystem;

    @PostMapping("/purchase")
    public ResponseEntity<String> purchase(@RequestBody PurchaseRequest purchaseRequest) {
        purchaseSystem.purchase(purchaseRequest);
        return ResponseEntity.ok("구매 성공");
    }
}
