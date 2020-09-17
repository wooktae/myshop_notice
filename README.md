# 마이 샾 : myShop

## 서비스 소스 레파지토리
- https://github.com/wooktae/myshop_customercenter.git
- https://github.com/wooktae/myshop_order.git
- https://github.com/wooktae/myshop_delivery.git
- https://github.com/wooktae/myshop_gateway.git
- https://github.com/wooktae/myshop_notice.git

## 서비스 시나리오

### 기능적 요구사항
1. 고객이 상품을 주문한다.
2. 주문과 동시에 배송 정보가 넘어가고, 배송이 된다는 알림을 받게 된다.
3. 고객은 고객센터에서 자신의 주문 상태를 확인할 수 있다.
4. 주문 취소는 배송 취소시에만 가능하고, 상태를 확인할 수 있어야 한다.
5. 주문 취소 시 취소 되었다는 알림을 받게 된다.

### 비기능적 요구사항

#### 트랜잭션
 - 주문 취소는 배송 취소가 선행되어야 한다. Sync 호출(Req/Res)

#### 장애격리
 - 주문은 배송이나 알림 서비스가 되지 않더라도 되어야 한다. Async (event-driven)

## 이벤트스토밍

<img src="https://user-images.githubusercontent.com/20061192/93415849-6985e700-f8df-11ea-9f85-3299e31aeafa.png" width="90%"></img>

## 구현
- 분석/설계 단계에서 도출된 헥사고날 아키텍처에 따라, 각 BC별로 대변되는 마이크로 서비스들을 스프링부트로 구현
- 구현한 각 서비스를 로컬에서 실행하는 방법은 아래와 같다 (각자의 포트넘버는 8081 ~ 8084 이다)

### DDD 의 적용
- 각 서비스내에 도출된 핵심 객체를 Entity 로 선언
  - 주문 -> order
  - 배송 -> delivery
  - 주문취소 -> cancellation
  - 알림 -> notice

### 적용 후 REST API 의 테스트

- 상품 주문 : http POST http://localhost:8081/orders productId=1 qty=10
- 상품 주문 취소 : http DELETE http://localhost:8081/orders/1
- 상품 주문 확인 : http GET http://localhost:8081/orders/1
- 배송 확인 : http GET http://localhost:8082/deliveries/1
- 알림 등록 : http POST http://localhost:8084/notices orderId=1 notiStatus="new notification"
- 알림 확인 : http GET http://localhost:8084/notices/1
- 고객 센터 확인 : http GET http://localhost:8083/customercenter/myPages

## SAGA 패턴

- 주문을 하면 배송이 되고 주문 상태값이 SHIPPED 로 변경된다. 
- 주문을 취소하면 배송이 취소 되고 주문 상태값이 DeliveryCancelled 로 변경된다.
- 주문을 하면 배송 알림이 나가고 알림 상태값이 Notice Send 로 변경된다.
- 주문을 취소하면 배송 취소 알림이 나가고 알림 상태값이 Cancel Notice Send 로 변경된다.

<img src="https://user-images.githubusercontent.com/20061192/93415879-799dc680-f8df-11ea-9e7b-1839fd21f323.png" width="90%"></img>

<img src="https://user-images.githubusercontent.com/20061192/93415883-7c002080-f8df-11ea-8d62-cf68fd6e49bd.png" width="90%"></img>

<img src="https://user-images.githubusercontent.com/20061192/93417147-47419880-f8e2-11ea-91e9-f0391f71509a.png" width="90%"></img>

## CQRS

- 고객은 자신의 주문 상태를 뷰(Page) 를 통해 확인할 수 있다. 
<img src="https://user-images.githubusercontent.com/20061192/93415924-94703b00-f8df-11ea-92cc-5232ad62d8a1.png"></img>
<img src="https://user-images.githubusercontent.com/20061192/93415916-8de1c380-f8df-11ea-82c6-5de65d39da00.png"></img>

## 동기식 호출 

- 배송전 알림을 보내는 부분을 FeignClient를 사용하여 동기식 트랜잭션으로 처리 

<img src="https://user-images.githubusercontent.com/20061192/93417648-9936ee00-f8e3-11ea-8014-7b6279bfe5a1.png"></img>

<img src="https://user-images.githubusercontent.com/20061192/93417657-9b994800-f8e3-11ea-98de-950c38032fea.png"></img>


## 폴리글랏

- view페이지인 customercenter 서비스에서는 DB hsql를 적용함

<img src="https://user-images.githubusercontent.com/68719151/93408246-0724ea80-f8cf-11ea-81c5-17fd29f96ba5.JPG"></img>

## 서킷 브레이킹

 - istio를 이용한 CB TEST 

<img src="https://user-images.githubusercontent.com/68719151/93408579-e3ae6f80-f8cf-11ea-8459-ac5f7ef7cd08.JPG"></img>

<img src="https://user-images.githubusercontent.com/68719151/93408602-ec06aa80-f8cf-11ea-8ee6-a48d9c01a7d7.JPG"></img>

<img src="https://user-images.githubusercontent.com/68719151/93408584-e5783300-f8cf-11ea-9bf1-982a5e87ebc5.JPG"></img>

<img src="https://user-images.githubusercontent.com/68719151/93408587-e6a96000-f8cf-11ea-8834-f284425f45ff.JPG"></img>


## 오토스케일 아웃

 - Mertric 서버 설치 후 오토스케일링 TEST
 - reservation의 deployment.yaml수정 후 배포 

<img src="https://user-images.githubusercontent.com/68719151/93408895-9f6f9f00-f8d0-11ea-9c2c-e40e4335e90b.JPG"></img>

<img src="https://user-images.githubusercontent.com/68719151/93408910-aac2ca80-f8d0-11ea-90ff-75d3c01f52b8.JPG"></img>

<img src="https://user-images.githubusercontent.com/68719151/93408953-c1692180-f8d0-11ea-9ef0-dde5cf3e84e4.JPG"></img>

<img src="https://user-images.githubusercontent.com/68719151/93408929-b4e4c900-f8d0-11ea-8d57-8013d1d2840a.JPG"></img>

<img src="https://user-images.githubusercontent.com/68719151/93408943-bc0bd700-f8d0-11ea-9fa9-44cea466bf4b.JPG"></img>

<img src="https://user-images.githubusercontent.com/68719151/93408956-c332e500-f8d0-11ea-9b59-cd46fe189dd2.JPG"></img>

