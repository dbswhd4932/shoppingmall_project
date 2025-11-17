# 결제 모듈 통합 가이드

## 개요

장바구니에서 주문으로 전환하는 플로우에 TossPayments 결제 모듈을 통합했습니다.
인터페이스 기반 설계로 다른 PG사(아임포트, KCP 등)로 쉽게 교체 가능합니다.

## 아키텍처

```
┌─────────────────────────────────────────────────────────────┐
│                        Frontend (React)                      │
└─────────────────────────────────────────────────────────────┘
                           │
                           │ 1. 주문 생성 요청
                           ▼
┌─────────────────────────────────────────────────────────────┐
│                     OrderController                          │
│                   POST /api/orders                          │
└─────────────────────────────────────────────────────────────┘
                           │
                           │ 2. 결제 준비 요청
                           ▼
┌─────────────────────────────────────────────────────────────┐
│                    PaymentService (Interface)                │
│         ┌──────────────────────────────────────┐            │
│         │   TossPaymentService (구현체)         │            │
│         │   - requestPayment()                  │            │
│         │   - confirmPayment()                  │            │
│         │   - cancelPayment()                   │            │
│         │   - getPaymentStatus()                │            │
│         └──────────────────────────────────────┘            │
└─────────────────────────────────────────────────────────────┘
                           │
                           │ 3. PG사 API 호출
                           ▼
┌─────────────────────────────────────────────────────────────┐
│                    TossPayments API                          │
│              https://api.tosspayments.com/v1/payments        │
└─────────────────────────────────────────────────────────────┘
```

## 데이터 모델

### Payment 엔티티

```java
@Entity
public class Payment extends BaseTimeEntity {
    private Long id;                  // PK
    private UUID merchantId;          // 주문번호 (Order와 동일)
    private String paymentKey;        // PG사 결제 키
    private String orderId;           // PG사 주문 ID
    private Integer amount;           // 결제 금액
    private PaymentStatus status;     // 결제 상태
    private String method;            // 결제 수단
    private LocalDateTime approvedAt; // 승인 시간
    private String receiptUrl;        // 영수증 URL
    private String failReason;        // 실패 사유
    private String cancelReason;      // 취소 사유

    @OneToOne
    private Order order;              // 연결된 주문
}
```

### PaymentStatus Enum

```java
public enum PaymentStatus {
    READY,              // 결제 대기
    IN_PROGRESS,        // 결제 진행 중
    DONE,               // 결제 완료
    CANCELED,           // 결제 취소
    FAILED              // 결제 실패
}
```

## API 명세

### 1. 결제 준비 (주문 생성 시 자동 호출)

```
POST /api/payments/prepare
Authorization: Bearer {jwtToken}
Content-Type: application/json

Request Body:
{
    "merchantId": "550e8400-e29b-41d4-a716-446655440000",
    "amount": 50000,
    "orderName": "ORDER-20251117-A1B2C3 외 2건",
    "customerEmail": "user@example.com",
    "customerName": "홍길동",
    "paymentMethod": "CARD"
}

Response: 201 Created
{
    "merchantId": "550e8400-e29b-41d4-a716-446655440000",
    "orderId": "550e8400-e29b-41d4-a716-446655440000",
    "amount": 50000,
    "status": "READY",
    "method": "CARD"
}
```

### 2. 결제 승인 (사용자가 결제 완료 후 호출)

```
POST /api/payments/confirm
Authorization: Bearer {jwtToken}
Content-Type: application/json

Request Body:
{
    "paymentKey": "tviva20241117000000000000",
    "orderId": "550e8400-e29b-41d4-a716-446655440000",
    "amount": 50000
}

Response: 200 OK
{
    "merchantId": "550e8400-e29b-41d4-a716-446655440000",
    "paymentKey": "tviva20241117000000000000",
    "orderId": "550e8400-e29b-41d4-a716-446655440000",
    "amount": 50000,
    "status": "DONE",
    "method": "CARD",
    "approvedAt": "2025-11-17T10:30:00",
    "receiptUrl": "https://..."
}
```

### 3. 결제 취소

```
POST /api/payments/cancel
Authorization: Bearer {jwtToken}
Content-Type: application/json

Request Body:
{
    "paymentKey": "tviva20241117000000000000",
    "cancelReason": "고객 요청"
}

Response: 200 OK
{
    "merchantId": "550e8400-e29b-41d4-a716-446655440000",
    "paymentKey": "tviva20241117000000000000",
    "status": "CANCELED"
}
```

### 4. 결제 상태 조회

```
GET /api/payments/{paymentKey}
Authorization: Bearer {jwtToken}

Response: 200 OK
{
    "merchantId": "550e8400-e29b-41d4-a716-446655440000",
    "paymentKey": "tviva20241117000000000000",
    "amount": 50000,
    "status": "DONE",
    "method": "CARD"
}
```

## 주문 생성 플로우 (결제 통합)

```
1. 프론트엔드: 장바구니에서 상품 선택
   └─> POST /api/orders (주문 생성 요청)

2. 백엔드: OrderServiceImpl.cartOrder()
   ├─> PaymentService.requestPayment() 호출
   │   └─> Payment 엔티티 생성 (READY 상태)
   │
   ├─> Order 엔티티 생성
   │
   ├─> OrderItem 생성 (상품 정보)
   │
   ├─> 장바구니에서 주문된 상품 삭제
   │
   └─> RabbitMQ 이벤트 발행 (주문 알림)

3. 프론트엔드: TossPayments SDK로 결제창 호출
   (클라이언트에서 직접 처리)

4. 사용자: 결제 정보 입력 (카드번호, CVC 등)

5. TossPayments: 결제 성공 시 성공 URL로 리다이렉트
   └─> http://localhost:3000/payment/success?paymentKey=xxx&orderId=xxx&amount=xxx

6. 프론트엔드: 성공 URL에서 파라미터 추출
   └─> POST /api/payments/confirm (결제 승인 요청)

7. 백엔드: PaymentService.confirmPayment()
   ├─> TossPayments API로 최종 승인 요청
   │
   └─> Payment 상태를 DONE으로 변경

8. 프론트엔드: 주문 완료 페이지 표시
```

## 설정 방법

### 1. application.yml에 TossPayments 설정 추가

```yaml
# application-local.yml
payment:
  toss:
    secret-key: test_sk_zXLkKEypNArWmo50nX3lmeaxYG5R  # 테스트용 키
    api-url: https://api.tosspayments.com/v1/payments
    success-url: http://localhost:3000/payment/success
    fail-url: http://localhost:3000/payment/fail

# application-prod.yml (운영 환경)
payment:
  toss:
    secret-key: ${TOSS_SECRET_KEY}  # 환경 변수로 관리
    api-url: https://api.tosspayments.com/v1/payments
    success-url: https://yourdomain.com/payment/success
    fail-url: https://yourdomain.com/payment/fail
```

### 2. TossPayments 가입 및 키 발급

1. https://www.tosspayments.com/ 접속
2. 회원가입 후 상점 생성
3. 개발자 센터에서 API 키 발급
   - 테스트 시크릿 키: 개발/테스트용
   - 운영 시크릿 키: 실제 결제용
4. application.yml에 키 설정

### 3. 데이터베이스 마이그레이션

Payment 테이블과 Order의 payment 연관관계가 자동 생성됩니다.
(JPA ddl-auto: update 설정 시)

```sql
CREATE TABLE payment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    merchant_id VARCHAR(36) NOT NULL,
    payment_key VARCHAR(255) UNIQUE,
    order_id VARCHAR(255),
    amount INT NOT NULL,
    status VARCHAR(20) NOT NULL,
    method VARCHAR(20),
    approved_at DATETIME,
    receipt_url VARCHAR(500),
    fail_reason VARCHAR(500),
    cancel_reason VARCHAR(500),
    order_id_fk BIGINT,
    crated_at DATETIME,
    updated_at DATETIME,
    FOREIGN KEY (order_id_fk) REFERENCES orders(order_id)
);
```

## 프론트엔드 통합 예시 (React)

### 1. TossPayments SDK 설치

```bash
npm install @tosspayments/payment-sdk
```

### 2. 결제 요청 컴포넌트

```javascript
import { loadTossPayments } from '@tosspayments/payment-sdk';

const CheckoutPage = () => {
  const handlePayment = async () => {
    // 1. 주문 생성 (결제 준비 포함)
    const orderResponse = await api.post('/api/orders', {
      name: '홍길동',
      phone: '010-1234-5678',
      zipcode: '12345',
      detailAddress: '서울시...',
      totalPrice: 50000,
      merchantId: uuidv4(),
      orderItemCreates: [...] // 장바구니 아이템
    });

    // 2. TossPayments SDK 로드
    const tossPayments = await loadTossPayments(
      'test_ck_D5GePWvyJnrK0W0k6q8gLzN97Eoq' // 클라이언트 키
    );

    // 3. 결제창 호출
    await tossPayments.requestPayment('카드', {
      amount: 50000,
      orderId: orderResponse.data.merchantId,
      orderName: '노트북 외 2건',
      customerName: '홍길동',
      successUrl: 'http://localhost:3000/payment/success',
      failUrl: 'http://localhost:3000/payment/fail',
    });
  };

  return <button onClick={handlePayment}>결제하기</button>;
};
```

### 3. 결제 성공 페이지

```javascript
const PaymentSuccessPage = () => {
  const [searchParams] = useSearchParams();

  useEffect(() => {
    const confirmPayment = async () => {
      const paymentKey = searchParams.get('paymentKey');
      const orderId = searchParams.get('orderId');
      const amount = searchParams.get('amount');

      try {
        // 결제 승인 요청
        const response = await api.post('/api/payments/confirm', {
          paymentKey,
          orderId,
          amount: parseInt(amount)
        });

        alert('결제가 완료되었습니다!');
        navigate('/orders');
      } catch (error) {
        alert('결제 승인 실패: ' + error.message);
        navigate('/payment/fail');
      }
    };

    confirmPayment();
  }, []);

  return <div>결제 처리 중...</div>;
};
```

## 다른 PG사로 교체하는 방법

### 1. PaymentService 인터페이스 구현체 추가

```java
@Service
@RequiredArgsConstructor
public class IamportPaymentService implements PaymentService {

    @Override
    public PaymentResponse requestPayment(PaymentRequest request) {
        // 아임포트 API 호출 로직
    }

    @Override
    public PaymentResponse confirmPayment(PaymentConfirmRequest request) {
        // 아임포트 결제 승인 로직
    }

    // ... 나머지 메서드 구현
}
```

### 2. application.yml에서 선택

```yaml
payment:
  provider: toss  # toss, iamport, kcp 등
```

### 3. 조건부 빈 등록

```java
@Configuration
public class PaymentConfig {

    @Bean
    @ConditionalOnProperty(name = "payment.provider", havingValue = "toss")
    public PaymentService tossPaymentService() {
        return new TossPaymentService(...);
    }

    @Bean
    @ConditionalOnProperty(name = "payment.provider", havingValue = "iamport")
    public PaymentService iamportPaymentService() {
        return new IamportPaymentService(...);
    }
}
```

## 테스트 방법

### 1. 단위 테스트

```java
@SpringBootTest
class TossPaymentServiceTest {

    @Autowired
    private PaymentService paymentService;

    @Test
    void 결제_준비_테스트() {
        PaymentRequest request = new PaymentRequest(
            UUID.randomUUID(),
            50000,
            "테스트 주문",
            "test@example.com",
            "테스터",
            "CARD"
        );

        PaymentResponse response = paymentService.requestPayment(request);

        assertThat(response.getStatus()).isEqualTo("READY");
        assertThat(response.getAmount()).isEqualTo(50000);
    }
}
```

### 2. TossPayments 테스트 카드

```
카드번호: 5272-2809-0000-1234
유효기간: 12/26
CVC: 123
비밀번호 앞 2자리: 12
```

## 보안 고려사항

### 1. Secret Key 관리
- **절대로 클라이언트에 노출하지 말 것**
- 환경 변수로 관리 (`${TOSS_SECRET_KEY}`)
- Git에 커밋하지 말 것 (.gitignore 추가)

### 2. 결제 금액 검증
```java
// 클라이언트에서 전달받은 금액과 실제 주문 금액 비교
if (!request.getAmount().equals(order.getTotalPrice())) {
    throw new BusinessException(PAYMENT_AMOUNT_MISMATCH);
}
```

### 3. 중복 결제 방지
```java
// merchantId(주문번호)로 이미 결제 완료된 건 체크
Payment existingPayment = paymentRepository.findByMerchantId(merchantId);
if (existingPayment != null && existingPayment.isDone()) {
    throw new BusinessException(PAYMENT_ALREADY_DONE);
}
```

## 트러블슈팅

### 1. "결제 정보를 찾을 수 없습니다" 오류
- Payment 엔티티가 생성되지 않음
- OrderServiceImpl에서 paymentService.requestPayment() 호출 확인

### 2. TossPayments API 401 Unauthorized
- Secret Key가 잘못됨
- Base64 인코딩 확인
- 테스트 키 vs 운영 키 확인

### 3. 결제 승인 후에도 READY 상태
- confirmPayment() 호출이 안 됨
- 프론트엔드에서 성공 URL로 리다이렉트 후 승인 API 호출 확인

## 참고 문서

- [TossPayments 공식 문서](https://docs.tosspayments.com/)
- [TossPayments API Reference](https://docs.tosspayments.com/reference)
- [결제 플로우 가이드](https://docs.tosspayments.com/guides/payment-flow)

## 향후 개선 사항

1. **웹훅 처리**: TossPayments에서 결제 상태 변경 시 자동 알림
2. **결제 내역 조회**: 마이페이지에서 결제 내역 확인
3. **부분 취소**: 주문 아이템별 부분 취소 지원
4. **정기 결제**: 구독 서비스 지원
5. **다중 결제 수단**: 카드, 계좌이체, 간편결제 등

---

**작성일**: 2025-11-17
**버전**: 1.0.0
