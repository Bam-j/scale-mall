package com.joo.scalemall.dto;

//상품 구매 요청 시 상품 정보 전송 데이터 DTO
public record PurchasePayload(
    Long productId,
    Long remainingStock
) { }

