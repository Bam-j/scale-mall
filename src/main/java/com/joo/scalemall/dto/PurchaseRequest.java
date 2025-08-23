package com.joo.scalemall.dto;

public record PurchaseRequest(
    String clientId,
    Long productId,
    Integer quantity
) { }
