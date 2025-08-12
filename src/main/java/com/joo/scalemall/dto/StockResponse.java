package com.joo.scalemall.dto;

public record StockResponse(
    Long id,
    Long stock,
    Boolean isSoldOut
) { }
