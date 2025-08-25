package com.joo.scalemall.dto;

//상세 페이지 등에서 사용하는 상품 정보 응답 DTO
public record ProductResponse(
    Long id,
    String name,
    Long price,
    String description,
    Long stock
) { }
