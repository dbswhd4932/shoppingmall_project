# Slack 알림 설정 가이드

> 주문 생성 시 Slack으로 실시간 알림을 받는 방법

## 📋 목차
1. [개요](#개요)
2. [Slack Webhook URL 생성](#slack-webhook-url-생성)
3. [설정 방법](#설정-방법)
4. [테스트](#테스트)
5. [문제 해결](#문제-해결)

---

## 개요

### 기능 설명
- 주문이 생성되면 **RabbitMQ → Consumer → Slack Webhook** 순서로 비동기 알림 전송
- 주문 처리와 분리되어 있어 Slack 장애가 주문에 영향을 주지 않음
- ELK 로그에도 모든 과정이 기록됨

### 동작 흐름
```
1. 주문 생성 (OrderServiceImpl)
   ↓
2. RabbitMQ에 이벤트 발행 (OrderEventPublisher)
   ↓
3. Consumer가 메시지 수신 (OrderNotificationConsumer)
   ↓
4. Slack Webhook 호출 (SlackNotificationService)
   ↓
5. Slack 채널에 알림 표시
```

---

## Slack Webhook URL 생성

### 1단계: Slack 워크스페이스 접속
- Slack 워크스페이스에 로그인
- 브라우저에서 워크스페이스 설정 페이지로 이동

### 2단계: Incoming Webhooks 앱 추가
1. [Slack API 페이지](https://api.slack.com/apps) 접속
2. **"Create New App"** 클릭
3. **"From scratch"** 선택
4. App 이름 입력 (예: `주문 알림 봇`)
5. 워크스페이스 선택 후 **"Create App"** 클릭

### 3단계: Incoming Webhooks 활성화
1. 왼쪽 메뉴에서 **"Incoming Webhooks"** 클릭
2. **"Activate Incoming Webhooks"** 토글을 **ON**으로 변경
3. 페이지 하단의 **"Add New Webhook to Workspace"** 클릭
4. 알림을 받을 채널 선택 (예: `#주문알림`, `#일반`)
5. **"Allow"** 클릭

### 4단계: Webhook URL 복사
- 생성된 Webhook URL 복사 (형식: `https://hooks.slack.com/services/T00/B00/XXXX`)
- 이 URL을 application-local.yml에 설정

---

## 설정 방법

### 1. .env 파일 생성 및 설정

파일 경로: 프로젝트 루트의 `.env` 파일

```bash
# .env 파일이 없다면 .env.example을 복사하여 생성
cp .env.example .env
```

`.env` 파일 내용:
```bash
SLACK_WEBHOOK_URL=https://hooks.slack.com/services/YOUR/WEBHOOK/URL
```

**⚠️ 주의사항:**
- `.env` 파일은 `.gitignore`에 포함되어 Git에 커밋되지 않습니다
- **절대 .env 파일을 Git에 푸시하지 마세요!** (보안 위험)
- 팀원과 공유 시 `.env.example` 파일을 참고하도록 안내

### 2. application-local.yml 확인

파일 경로: `src/main/resources/application-local.yml`

```yaml
# Slack 알림 설정
slack:
  webhook:
    url: ${SLACK_WEBHOOK_URL}  # .env 파일의 환경변수 사용
    enabled: true  # Slack 알림 활성화
```

**참고:**
- `${SLACK_WEBHOOK_URL}`: .env 파일의 환경변수를 자동으로 로드
- `enabled: true`: Slack 알림 활성화
- `enabled: false`: Slack 알림 비활성화 (개발 중 테스트 시 유용)

### 3. 의존성 다운로드 및 빌드
```bash
# Gradle 의존성 다운로드 (dotenv-java 라이브러리 포함)
./gradlew clean build
```

### 4. 서버 재시작
```bash
# 서버 중지 (실행 중인 경우)
Ctrl + C

# 서버 재시작
./gradlew bootRun --args='--spring.profiles.active=local'
```

---

## 테스트

### 1. RabbitMQ 실행 확인
```bash
docker ps | grep rabbitmq
```

출력 예시:
```
CONTAINER ID   IMAGE                    STATUS
abc123def456   rabbitmq:3.12-management Up 2 hours
```

### 2. 주문 생성 테스트

#### API로 주문 생성
```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "merchantId": "ORDER-20251125-ABC123",
    "totalPrice": 50000,
    "orderItemCreates": [
      {
        "goodsId": 1,
        "amount": 2,
        "orderPrice": 50000,
        "optionNumber": null
      }
    ],
    "name": "홍길동",
    "phone": "010-1234-5678",
    "zipcode": "12345",
    "detailAddress": "서울시 강남구",
    "requirement": "문 앞에 놓아주세요",
    "cardCompany": "신한카드",
    "cardNumber": "1234-5678-****-****",
    "impUid": "imp_123456789"
  }'
```

### 3. 로그 확인

#### 콘솔 로그
```
[RabbitMQ] 주문 생성 이벤트 발행 시작: orderId=1, merchantId=ORDER-20251125-ABC123
[RabbitMQ] 주문 생성 이벤트 발행 성공
================================================================================
[RabbitMQ Consumer] 주문 생성 이벤트 수신 시작
주문 ID: 1
주문 번호: ORDER-20251125-ABC123
총 주문 금액: ₩50,000
================================================================================
[Slack] 주문 알림 전송 시작: orderId=1
[Slack] 주문 알림 전송 성공: orderId=1
```

#### ELK 로그 (Kibana)
```
Kibana → Discover → 검색어: "Slack 주문 알림"
```

### 4. Slack 채널 확인
- 설정한 Slack 채널에 알림이 표시되는지 확인
- 알림 메시지 형식:
  ```
  🛒 새로운 주문 알림
  ━━━━━━━━━━━━━━━━━━━━
  주문 ID: 1
  주문 번호: ORDER-20251125-ABC123
  주문자: user123
  이메일: user@example.com
  주문 금액: ₩50,000
  주문 상태: ORDER_COMPLETE
  주문 시간: 2025-11-25 14:30:00
  ```

---

## 문제 해결

### 1. Slack 알림이 안 옴

**증상:**
- 주문은 정상 생성됨
- RabbitMQ Consumer 로그는 출력됨
- 하지만 Slack에 알림이 안 옴

**해결 방법:**

#### A. .env 파일 확인
```bash
# .env 파일 확인
cat .env
```

출력이 다음과 같아야 함:
```bash
SLACK_WEBHOOK_URL=https://hooks.slack.com/services/T09.../B09.../...
```

**체크리스트:**
- [ ] `.env` 파일이 프로젝트 루트에 존재하는가?
- [ ] `SLACK_WEBHOOK_URL`이 실제 Slack Webhook URL인가?
- [ ] URL에 오타가 없는가?
- [ ] `application-local.yml`의 `enabled: true`인가?

#### B. 수동으로 Webhook 테스트
```bash
curl -X POST YOUR_WEBHOOK_URL \
  -H "Content-Type: application/json" \
  -d '{
    "text": "테스트 메시지입니다!"
  }'
```

성공하면 Slack에 "테스트 메시지입니다!" 표시됨.

#### C. 로그에서 에러 확인
```bash
# 로그 파일 확인
tail -f logs/shoppingmall.log | grep Slack
```

일반적인 에러:
- `401 Unauthorized`: Webhook URL이 잘못됨
- `404 Not Found`: Webhook URL이 만료되었거나 삭제됨
- `Connection refused`: 네트워크 문제

### 2. Consumer 로그가 안 나옴

**증상:**
- `[RabbitMQ] 주문 생성 이벤트 발행 성공` 로그는 나옴
- `[RabbitMQ Consumer] 주문 생성 이벤트 수신 시작` 로그는 안 나옴

**해결 방법:**

#### A. RabbitMQ 확인
```bash
# RabbitMQ Management UI 접속
http://localhost:15672

# 로그인: admin / admin1234
# Queues 탭 → order.notification.queue 확인
# - Ready: 처리 대기 중인 메시지 수
# - Unacked: 처리 중인 메시지 수
```

**메시지가 쌓여있다면:**
- Consumer가 실행되지 않은 것
- 서버 재시작 필요

#### B. RabbitMQ Connection 확인
```bash
# 로그에서 RabbitMQ 연결 확인
tail -f logs/shoppingmall.log | grep -i "rabbit\|amqp"
```

정상 연결 시:
```
Created new connection: rabbitConnectionFactory#...
Channel created
```

연결 실패 시:
```
Failed to connect to RabbitMQ
```

**연결 실패 해결:**
```bash
# RabbitMQ 재시작
docker restart rabbitmq_container_name
```

### 3. Slack 알림이 비활성화됨

**증상:**
```
[Slack] Slack 알림이 비활성화되어 있습니다 (slack.webhook.enabled=false)
```

**해결 방법:**
```yaml
# application-local.yml
slack:
  webhook:
    enabled: true  # ← false → true로 변경
```

서버 재시작 필요.

### 4. 주문이 생성되지 않음

**증상:**
- 주문 API 호출 시 에러 발생
- Slack 알림 이전에 주문 자체가 실패

**해결 방법:**

#### A. JWT 토큰 확인
```bash
# 로그인 후 토큰 발급
curl -X POST http://localhost:8080/api/members/login \
  -H "Content-Type: application/json" \
  -d '{
    "loginId": "user123",
    "password": "password123"
  }'
```

응답:
```json
{
  "jwtToken": "eyJhbGc...",
  "loginId": "user123",
  "role": "ROLE_USER"
}
```

#### B. 상품 ID 확인
```bash
# 상품 목록 조회
curl http://localhost:8080/api/goods
```

존재하는 `goodsId`를 주문 요청에 사용.

---

## 커스터마이징

### 1. 알림 메시지 변경

파일: `src/main/java/com/project/shop/notification/service/SlackNotificationService.java`

```java
// 메시지 내용 수정
private SlackMessage buildOrderMessage(OrderCreatedEvent event) {
    return SlackMessage.builder()
            .text("🎉 신규 주문! 총 " + formatPrice(event.getTotalPrice()))  // ← 수정
            .blocks(...)
            .build();
}
```

### 2. 조건부 알림 (고액 주문만)

```java
public void sendOrderNotification(OrderCreatedEvent event) {
    // 10만원 이상 주문만 Slack 알림
    if (event.getTotalPrice() < 100000) {
        log.info("[Slack] 주문 금액이 10만원 미만이므로 알림 생략");
        return;
    }

    // Slack 전송
    ...
}
```

### 3. 다른 채널로 분기

```yaml
# application-local.yml
slack:
  webhook:
    vip-url: https://hooks.slack.com/services/VIP/WEBHOOK  # VIP 고객용
    normal-url: https://hooks.slack.com/services/NORMAL/WEBHOOK  # 일반 고객용
```

```java
// 주문 금액에 따라 다른 채널로 전송
if (event.getTotalPrice() >= 1000000) {
    sendMessage(vipWebhookUrl, message);
} else {
    sendMessage(normalWebhookUrl, message);
}
```

---

## 주요 파일 위치

| 파일 | 경로 | 역할 |
|------|------|------|
| **설정 파일** | `src/main/resources/application-local.yml` | Webhook URL, enabled 설정 |
| **Slack 서비스** | `src/main/java/com/project/shop/notification/service/SlackNotificationService.java` | Webhook 호출 로직 |
| **Slack DTO** | `src/main/java/com/project/shop/notification/dto/SlackMessage.java` | Slack 메시지 구조 |
| **Consumer** | `src/main/java/com/project/shop/order/consumer/OrderNotificationConsumer.java` | RabbitMQ 메시지 수신 및 Slack 호출 |
| **Event** | `src/main/java/com/project/shop/order/event/OrderCreatedEvent.java` | 주문 이벤트 DTO |
| **Publisher** | `src/main/java/com/project/shop/order/publisher/OrderEventPublisher.java` | RabbitMQ 메시지 발행 |

---

## 참고 자료

- [Slack API - Incoming Webhooks](https://api.slack.com/messaging/webhooks)
- [Slack Block Kit Builder](https://app.slack.com/block-kit-builder) - 메시지 디자인 도구
- [RabbitMQ Management](http://localhost:15672) - Queue 모니터링
- [프로젝트 README](./CLAUDE.md) - 전체 시스템 아키텍처

---

**작성일**: 2025.11.25
**작성자**: Claude Code
**버전**: 1.0.0
