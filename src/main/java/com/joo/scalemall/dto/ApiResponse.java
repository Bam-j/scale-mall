package com.joo.scalemall.dto;

import java.time.Instant;

//공통 응답 DTO
public record ApiResponse<T>(
    String status,
    String code,
    String message,
    T data,
    Meta meta
) {

    public static <T> ApiResponse<T> ok(String code, String message, T data, String path) {
        return new ApiResponse<>("OK", code, message, data, Meta.now(path));
    }

    public static <T> ApiResponse<T> error(String code, String message, String path) {
        return new ApiResponse<>("ERROR", code, message, null, Meta.now(path));
    }

    public record Meta(Instant timestamp, String path) {

        public static Meta now(String path) {
            return new Meta(Instant.now(), path);
        }
    }
}

/*
//결과 응답 형태 예시
// 성공
{
  "status": "OK",
  "code": "PURCHASE_SUCCESS",
  "message": "구매 성공",
  "data": { "productId": 1, "remainingStock": 72 },
  "meta": { "timestamp": "2025-08-13T12:34:56Z", "path": "/api/products/purchase" }
}

// 실패
{
  "status": "ERROR",
  "code": "ALREADY_PURCHASED",
  "message": "이미 구매하셨습니다.",
  "data": null,
  "meta": { "timestamp": "2025-08-13T12:34:57Z", "path": "/api/products/purchase" }
}
 */
