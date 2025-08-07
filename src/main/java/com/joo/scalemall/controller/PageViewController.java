package com.joo.scalemall.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.joo.scalemall.dto.ProductResponse;
import com.joo.scalemall.service.ProductCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Mono;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class PageViewController {

    private final ProductCacheService productCacheService;
    private final ReactiveRedisTemplate reactiveRedisTemplate;
    private final ObjectMapper objectMapper;

    //메인 홈페이지
    @GetMapping
    public String index(Model model){
        String key = "product:1";

        Mono<ProductResponse> productResponseMono = reactiveRedisTemplate
            .opsForValue()
            .get(key)
            .flatMap(json -> {
                try {
                    ProductResponse productResponse = objectMapper.readValue(json.toString(), ProductResponse.class);
                    return Mono.just(productResponse);
                } catch (JsonProcessingException e) {
                    return Mono.error(e);
                }
            });

        ProductResponse productResponse = productResponseMono.block();
        model.addAttribute("product", productResponse);
        return "index";
    }

    //상품 상세 페이지
    @GetMapping("/detail/{id}")
    public String detailPage(@PathVariable Long id, Model model) {
        ProductResponse productResponse = productCacheService.getProductFromRedis(id).block();
        model.addAttribute("product", productResponse);
        return "detailPage";
    }
}
