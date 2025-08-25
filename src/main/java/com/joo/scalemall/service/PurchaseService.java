package com.joo.scalemall.service;

import com.joo.scalemall.dto.ApiResponse;
import com.joo.scalemall.dto.PurchasePayload;
import com.joo.scalemall.dto.PurchaseRequest;
import com.joo.scalemall.util.enums.ResultCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class PurchaseService {

    private final ProductCacheService productCacheService;

    //상품 구매 요청 처리
    public Mono<ResponseEntity<ApiResponse<PurchasePayload>>> purchase(
        PurchaseRequest purchaseRequest,
        ServerWebExchange exchange
    ) {
        Long id = purchaseRequest.productId();
        String path = exchange.getRequest().getPath().value();

        return productCacheService.decrementStockSafely(id)
            .flatMap(result -> {
                switch (result) {
                    case SUCCESS:
                        return productCacheService.getStock(id)
                            .map(remain -> ResponseEntity.ok(
                                ApiResponse.ok(
                                    ResultCode.PURCHASE_SUCCESS.name(),
                                    "구매 성공",
                                    new PurchasePayload(id, remain),
                                    path
                                )
                            ));

                    case OUT_OF_STOCK:
                        return Mono.just(ResponseEntity.status(409).body(
                            ApiResponse.error(ResultCode.OUT_OF_STOCK.name(), "품절", path)));

                    case NO_STOCK_KEY:
                    default:
                        return Mono.just(ResponseEntity.status(500).body(
                            ApiResponse.error(ResultCode.NO_STOCK_KEY.name(), "재고 키 없음", path)));
                }
            });
    }
}
