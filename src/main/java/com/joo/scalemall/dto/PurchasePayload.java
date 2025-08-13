package com.joo.scalemall.dto;

public record PurchasePayload(
    Long productId,
    Long remainingStock
) { }

