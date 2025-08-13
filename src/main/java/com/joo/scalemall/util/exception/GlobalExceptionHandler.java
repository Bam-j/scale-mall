package com.joo.scalemall.util.exception;

import com.joo.scalemall.dto.ApiResponse;
import com.joo.scalemall.util.enums.ResultCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public Mono<ResponseEntity<ApiResponse<Object>>> handleBadRequest(
        IllegalArgumentException ex, ServerWebExchange exchange
    ) {
        String path = exchange.getRequest().getPath().value();
        var body = ApiResponse.error(ResultCode.BAD_REQUEST.name(), ex.getMessage(), path);
        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body));
    }

    @ExceptionHandler(Throwable.class)
    public Mono<ResponseEntity<ApiResponse<Object>>> handleAny(
        Throwable ex, ServerWebExchange exchange
    ) {
        String path = exchange.getRequest().getPath().value();
        var body = ApiResponse.error(ResultCode.INTERNAL_ERROR.name(), "서버 오류", path);
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body));
    }
}
