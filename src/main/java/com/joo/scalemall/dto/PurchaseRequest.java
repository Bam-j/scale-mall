package com.joo.scalemall.dto;

//상품 구매 시 사용자의 요청 DTO
public record PurchaseRequest(
    String clientId,
    Long productId,
    Integer quantity
) { }
