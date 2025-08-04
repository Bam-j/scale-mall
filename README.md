# Scale Mall
- Redis를 활용한 대규모 트래픽 분산 처리 쇼핑몰 프로젝트

---

## 프로젝트 문제 제시 상황

- 쇼핑몰 내에 하나의 한정 상품을 두고 `100 * 10 ^ n` 명의 고객이 구매를 시도함
  - n = 1, 2, ..., 5
  - 상품은 총 `10 * 10 ^ n`개만 준비
- 트래픽 분산 처리 시스템 구현이 목적이므로 로그인 및 사용자 페이지 등의 서비스는 제공하지 않음

---

## 사용기술
- 프론트엔드: Thymeleaf, CSS
- 백엔드: Java, Spring Boot, Spring Webflux, Redis
- Docker, Apache JMeter