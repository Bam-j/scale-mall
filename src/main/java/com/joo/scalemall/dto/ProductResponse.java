package com.joo.scalemall.dto;

public record ProductResponse(
    Long id,
    String name,
    Long price,
    String description,
    Long stock
) { }
