package com.joo.scalemall.dto;

//상품 재고 확인 응답 DTO
public record StockResponse(
    Long id,
    Long stock,
    Boolean isSoldOut
) { }
