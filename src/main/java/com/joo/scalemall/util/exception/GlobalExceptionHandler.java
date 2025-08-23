package com.joo.scalemall.util.exception;

import com.joo.scalemall.dto.ApiResponse;
import com.joo.scalemall.util.enums.ResultCode;
import com.joo.scalemall.util.exception.custom.NoStockKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestControllerAdvice(basePackages = "com.joo.scalemall.controller")
public class GlobalExceptionHandler {

    private static boolean isActuator(ServerWebExchange exchange) {
        String path = exchange.getRequest().getPath().value();
        return path.startsWith("/actuator");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public Mono<ResponseEntity<ApiResponse<Object>>> handleBadRequest(
        IllegalArgumentException ex, ServerWebExchange exchange
    ) {
        if (isActuator(exchange)) {
            return Mono.error(ex);
        }
        var body = ApiResponse.error(ResultCode.BAD_REQUEST.name(), ex.getMessage(),
            exchange.getRequest().getPath().value());
        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body));
    }

    @ExceptionHandler(NoStockKeyException.class)
    public Mono<ResponseEntity<ApiResponse<Object>>> handleNoStock(
        NoStockKeyException ex, ServerWebExchange exchange
    ) {
        if (isActuator(exchange)) {
            return Mono.error(ex);
        }
        String path = exchange.getRequest().getPath().value();
        var body = ApiResponse.error("NO_STOCK_KEY", "재고 키 없음", path);
        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(body));
    }

    @ExceptionHandler(Throwable.class)
    public Mono<ResponseEntity<ApiResponse<Object>>> handleAny(
        Throwable ex, ServerWebExchange exchange
    ) {
        if (isActuator(exchange)) {
            return Mono.error(ex);
        }
        var body = ApiResponse.error(ResultCode.INTERNAL_ERROR.name(), "서버 오류",
            exchange.getRequest().getPath().value());
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body));
    }
}
