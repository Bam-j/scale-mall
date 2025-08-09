package com.joo.scalemall.service;

import com.joo.scalemall.dto.PurchaseRequest;
import org.springframework.stereotype.Service;

@Service
public class PurchaseService {

    public void purchase(PurchaseRequest purchaseRequest) {
        Long id = purchaseRequest.id();

        //TODO: 재고 감소, 중복 구매 방지
    }
}
