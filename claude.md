# OpenMarket ShoppingMall - 개발 기획서 & 명세서

> 이 문서는 프로젝트의 비즈니스 로직, API 명세, 데이터 모델을 정리한 기획서입니다.
> 새로운 기능 추가나 로직 수정 시 이 문서를 참고합니다.

**최종 수정일**: 2025.11.09

---

## 📋 목차
1. [프로젝트 개요](#프로젝트-개요)
2. [시스템 아키텍처](#시스템-아키텍처)
3. [권한 시스템](#권한-시스템)
4. [데이터 모델](#데이터-모델)
5. [API 명세](#api-명세)
6. [비즈니스 로직](#비즈니스-로직)
7. [프론트엔드 구조](#프론트엔드-구조)
8. [개발 환경 설정](#개발-환경-설정)
9. [향후 개발 계획](#향후-개발-계획)

---

## 프로젝트 개요

### 🎯 서비스 목표
- **C2C(Consumer to Consumer) 오픈마켓** 플랫폼
- 일반 사용자도 판매자로 전환하여 상품을 등록하고 판매할 수 있는 시스템
- 회원가입 → 상품 등록 → 장바구니 → 주문/결제 → 리뷰의 전체 쇼핑 플로우 구현

### 📊 기술 스택
- **Backend**: Spring Boot 2.7.5, Java 17, JPA/QueryDSL, Spring Security + JWT
- **Frontend**: React 18, React Router v6, Bootstrap 5, Axios
- **Database**: MySQL 8.0 (로컬: Docker, 운영: AWS RDS)
- **File Storage**: 로컬 파일 시스템 (개발), AWS S3 (운영 - 중단)
- **Deployment**: Docker, GitHub Actions CI/CD, AWS EC2 (중단)

### 🔑 핵심 특징
1. **다중 권한 시스템**: USER(구매자), SELLER(판매자), ADMIN(관리자)
2. **JWT 기반 무상태 인증**: MSA 환경을 고려한 확장 가능한 구조
3. **실시간 가격 검증**: 장바구니 → 주문 전환 시 가격 변동 감지
4. **소프트 딜리트**: 데이터 무결성 보장을 위한 논리적 삭제
5. **로컬 파일 저장**: 개발 환경에서 AWS 의존성 제거

---

## 시스템 아키텍처

### 전체 구조
```
Frontend (React:3000)
    ↓ REST API
Backend (Spring Boot:8080)
    ↓ JPA/QueryDSL
Database (MySQL:3306)
    ↓ File Upload
Local File System (/uploads)
```

### 패키지 구조
```
src/main/java/com/project/shop/
├── global/                 # 공통 설정 및 유틸리티
│   ├── config/            # Security, Swagger, Cache, Web 설정
│   ├── exception/         # 전역 예외 처리
│   └── common/            # BaseTimeEntity 등 공통 엔티티
├── member/                # 회원 도메인
│   ├── domain/           # Member 엔티티, RoleType Enum
│   ├── controller/       # 회원 API
│   ├── service/          # 회원 비즈니스 로직
│   └── repository/       # 회원 데이터 접근
├── goods/                # 상품 도메인
│   ├── domain/           # Goods, Category, Review, Reply 엔티티
│   ├── controller/       # 상품/카테고리 API
│   ├── service/          # 상품 비즈니스 로직
│   └── repository/       # 상품 데이터 접근
├── cart/                 # 장바구니 도메인
├── order/                # 주문 도메인
└── file/                 # 파일 관리 (LocalFileService)
```

### Frontend 구조
```
frontend/src/
├── api/                  # API 통신 (axios)
│   └── axios.js         # Axios 인스턴스, JWT 인터셉터
├── components/          # 공통 컴포넌트
│   └── Navbar.js       # 네비게이션 바 (로그인/로그아웃)
├── pages/              # 페이지 컴포넌트
│   ├── Home.js         # 홈 (상품 목록, 카테고리)
│   ├── Login.js        # 로그인
│   ├── Signup.js       # 회원가입
│   ├── MyPage.js       # 마이페이지
│   ├── Goods/
│   │   ├── GoodsList.js    # 상품 목록
│   │   ├── GoodsDetail.js  # 상품 상세
│   │   └── GoodsCreate.js  # 상품 등록
│   └── Category/
│       └── CategoryCreate.js  # 카테고리 생성
└── App.js              # 라우터 설정
```

---

## 권한 시스템

### 역할 정의 (RoleType Enum)
| 역할 | 설명 | 권한 레벨 |
|------|------|----------|
| **USER** | 일반 구매자 | 상품 조회, 장바구니, 주문, 리뷰 작성 |
| **SELLER** | 판매자 | USER 권한 + 상품 등록/수정/삭제, 대댓글 작성 |
| **ADMIN** | 관리자 | 모든 권한 + 카테고리 관리, 모든 데이터 접근 |

### 기능별 접근 제어
| 기능 | USER          | SELLER | ADMIN | 비로그인 |
|------|---------------|--------|-------|---------|
| 상품 조회/검색 | ✅             | ✅ | ✅ | ✅ |
| 회원가입/로그인 | ✅             | ✅ | ✅ | ✅ |
| 마이페이지 | ✅             | ✅ | ✅ | ❌ |
| 상품 등록/수정/삭제 | ❌             | ✅ | ✅ | ❌ |
| 카테고리 생성/수정/삭제 | ❌             | ❌ | ✅ | ❌ |
| 장바구니 관리 | ✅             | ✅ | ✅ | ❌ |
| 주문 생성/조회 | ✅             | ✅ | ✅ | ❌ |
| 리뷰 작성 | ✅ (구매한 상품만)   | ✅ | ✅ | ❌ |
| 대댓글 작성 | ✅ (본인 상품은 불가) | ✅ (본인 상품만) | ✅ | ❌ |

### Spring Security 설정
```java
// 예시: 상품 등록은 SELLER, ADMIN만 가능
@PreAuthorize("hasAnyRole('ROLE_SELLER','ROLE_ADMIN')")
public void goodsCreate(GoodsCreateRequest request) { ... }

// 예시: 장바구니/주문은 모든 인증 사용자 가능
@PreAuthorize("hasAnyRole('ROLE_USER','ROLE_SELLER','ROLE_ADMIN')")
public void cartAddGoods(CartCreateRequest request) { ... }
```

---

## 데이터 모델

### 핵심 엔티티 관계
```
Member (1) -----> (N) Goods         # 판매자가 등록한 상품
Member (1) -----> (N) Cart          # 사용자의 장바구니
Member (1) -----> (N) Order         # 사용자의 주문
Goods  (N) -----> (1) Category      # 상품은 하나의 카테고리
Goods  (1) -----> (N) Review        # 상품별 리뷰
Review (1) -----> (1) Reply         # 리뷰별 대댓글 (판매자 답변)
Order  (1) -----> (N) OrderItem     # 주문별 주문 아이템
Cart   (N) -----> (1) Goods         # 장바구니 아이템 - 상품
```

### Member (회원)
```java
@Entity
public class Member extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String loginId;          // 로그인 ID

    private String password;         // 비밀번호 (암호화)
    private String name;             // 이름
    private String email;            // 이메일
    private String phone;            // 전화번호 (선택)
    private String zipcode;          // 우편번호 (선택)
    private String detailAddress;    // 상세주소 (선택)

    @Enumerated(EnumType.STRING)
    private RoleType role;           // 권한 (USER, SELLER, ADMIN)

    private boolean deletedStatus;   // 삭제 여부 (소프트 딜리트)

    // BaseTimeEntity: createdAt, updatedAt 상속
}
```

### Goods (상품)
```java
@Entity
public class Goods extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String goodsName;        // 상품명
    private Integer price;           // 가격
    private String goodsDescription; // 상품 설명

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;           // 판매자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;       // 카테고리

    @OneToMany(mappedBy = "goods", cascade = CascadeType.ALL)
    private List<FileEntity> imageList; // 상품 이미지 목록

    // 상품 옵션 (JSON 형태로 저장)
    @Convert(converter = OptionListConverter.class)
    private List<OptionCreate> options;
}
```

### Category (카테고리)
```java
@Entity
public class Category {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long categoryId;

    @Column(unique = true)
    private String category;         // 카테고리명 (예: Electronics, Clothing)
}
```

### Cart (장바구니)
```java
@Entity
public class Cart extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;           // 사용자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "goods_id")
    private Goods goods;             // 상품

    private Integer amount;          // 수량
    private Integer optionNumber;    // 옵션 번호 (nullable)
}
```

### Order (주문)
```java
@Entity
@Table(name = "orders")
public class Order extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;           // 주문자

    private UUID merchantId;         // 주문번호 (고유)
    private Integer totalPrice;      // 총 결제 금액

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems; // 주문 아이템 목록

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus; // 주문 상태
}
```

### OrderItem (주문 아이템) - 반정규화 적용
```java
@Entity
public class OrderItem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "goods_id")
    private Goods goods;

    // 반정규화: 주문 시점의 상품 정보 저장 (상품 삭제되어도 주문 내역 유지)
    private String goodsName;        // 주문 당시 상품명
    private Integer goodsPrice;      // 주문 당시 상품 가격

    private Integer amount;          // 수량
    private Integer optionNumber;    // 옵션 번호
}
```

### Review & Reply (리뷰 & 대댓글)
```java
@Entity
public class Review extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;           // 리뷰 작성자

    @ManyToOne(fetch = FetchType.LAZY)
    private Goods goods;             // 리뷰 대상 상품

    private String content;          // 리뷰 내용
    private Integer rating;          // 평점 (1-5)

    @OneToOne(mappedBy = "review", cascade = CascadeType.ALL)
    private Reply reply;             // 판매자 답변
}

@Entity
public class Reply extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id")
    private Review review;           // 원본 리뷰

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;           // 판매자 (답변 작성자)

    private String content;          // 답변 내용
}
```

### FileEntity (파일)
```java
@Entity
public class FileEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;         // 원본 파일명
    private String filePath;         // 저장 경로
    private String fileUrl;          // 접근 URL (로컬: /uploads/xxx, S3: https://...)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "goods_id")
    private Goods goods;             // 소속 상품
}
```

---

## API 명세

### 🔐 인증 API

#### 회원가입
```
POST /api/members/signup
Content-Type: application/json

Request Body:
{
    "loginId": "user123",
    "password": "password123",
    "name": "홍길동",
    "email": "user@example.com",
    "phone": "010-1234-5678",    // 선택
    "zipcode": "12345",           // 선택
    "detailAddress": "서울시...",  // 선택
    "roleType": "USER"            // USER, SELLER 중 선택
}

Response: 201 Created
(응답 없음 - 성공 시 201 상태 코드만)
```

#### 로그인 ID 중복 확인
```
POST /api/members/exist
Content-Type: application/json

Request Body:
{
    "loginId": "user123"
}

Response: 200 OK
{
    "result": true   // true: 사용 가능, false: 중복
}
```

#### 로그인
```
POST /api/members/login
Content-Type: application/json

Request Body:
{
    "loginId": "user123",
    "password": "password123"
}

Response: 200 OK
{
    "jwtToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "loginId": "user123",
    "role": "ROLE_USER"
}

이후 모든 요청 헤더에 포함:
Authorization: Bearer {jwtToken}
```

#### 내 정보 조회
```
GET /api/members/me
Authorization: Bearer {jwtToken}

Response: 200 OK
{
    "loginId": "user123",
    "name": "홍길동",
    "email": "user@example.com",
    "phone": "010-1234-5678",
    "zipcode": "12345",
    "detailAddress": "서울시...",
    "roles": ["ROLE_USER"],
    "createdAt": "2025-11-01T10:00:00",
    "updatedAt": "2025-11-09T15:30:00"
}
```

#### 회원 정보 수정
```
PUT /api/members
Authorization: Bearer {jwtToken}
Content-Type: application/json

Request Body:
{
    "password": "newPassword123",  // 선택
    "name": "홍길동",
    "email": "newemail@example.com",
    "phone": "010-9999-9999",
    "zipcode": "54321",
    "detailAddress": "부산시..."
}

Response: 204 No Content
```

#### 회원 탈퇴
```
DELETE /api/members
Authorization: Bearer {jwtToken}

Response: 204 No Content
(소프트 딜리트: deletedStatus = true로 변경)
```

---

### 🛍 상품 API

#### 상품 등록 권한 체크
```
GET /api/goods/check-access
Authorization: Bearer {jwtToken}

Response: 200 OK
{
    "hasAccess": true
}

Error Responses:
- 401 Unauthorized: 로그인 필요
- 403 Forbidden: SELLER 권한 필요
```

#### 상품 등록
```
POST /api/goods
Authorization: Bearer {jwtToken}
Content-Type: multipart/form-data
@PreAuthorize: ROLE_SELLER, ROLE_ADMIN

Form Data:
- goodsName: "노트북"
- price: 1500000
- goodsDescription: "고성능 노트북입니다."
- categoryId: 1
- options: [{"key":"색상","value":"블랙"},{"key":"용량","value":"512GB"}]  // JSON 문자열
- images: [file1, file2, ...]  // 이미지 파일들

Response: 201 Created
```

#### 상품 목록 조회 (페이징)
```
GET /api/goods?page=0&size=12
(인증 불필요)

Response: 200 OK
{
    "content": [
        {
            "goodsId": 1,
            "goodsName": "노트북",
            "price": 1500000,
            "imageUrl": "http://localhost:8080/uploads/xxx.jpg",  // 대표 이미지
            "categoryName": "Electronics",
            "memberLoginId": "seller123"
        },
        ...
    ],
    "totalPages": 5,
    "totalElements": 48,
    "size": 12,
    "number": 0
}
```

#### 상품 상세 조회
```
GET /api/goods/{goodsId}
(인증 불필요)

Response: 200 OK
{
    "goodsId": 1,
    "goodsName": "노트북",
    "price": 1500000,
    "goodsDescription": "고성능 노트북입니다.",
    "memberLoginId": "seller123",
    "category": {
        "categoryId": 1,
        "category": "Electronics"
    },
    "imageList": [
        {
            "id": 1,
            "fileName": "laptop1.jpg",
            "fileUrl": "http://localhost:8080/uploads/xxx.jpg"
        },
        ...
    ],
    "options": [
        {"key": "색상", "value": "블랙"},
        {"key": "용량", "value": "512GB"}
    ]
}
```

#### 상품 검색 (키워드)
```
GET /api/goods/keyword?keyword=노트북&page=0&size=12
(인증 불필요)

Response: 200 OK (페이징 형태 동일)
```

#### 상품 검색 (가격 범위)
```
GET /api/goods/search?minPrice=100000&maxPrice=2000000&page=0&size=12
(인증 불필요)

Response: 200 OK (페이징 형태 동일)
```

#### 상품 가격 변경 확인 (장바구니 → 주문 시)
```
GET /api/goods/checkUpdateGoods?goodsId=1,2,3
Authorization: Bearer {jwtToken}

Response: 200 OK
{
    "result": false  // false: 가격 변동 없음, true: 가격 변동 있음
}
```

#### 상품 수정
```
POST /api/goods/{goodsId}
Authorization: Bearer {jwtToken}
Content-Type: multipart/form-data
@PreAuthorize: ROLE_SELLER, ROLE_ADMIN (본인 상품만)

Form Data: (등록과 동일)

Response: 204 No Content
```

#### 상품 삭제
```
DELETE /api/goods/{goodsId}
Authorization: Bearer {jwtToken}
@PreAuthorize: ROLE_SELLER, ROLE_ADMIN (본인 상품만)

Response: 204 No Content
```

---

### 📦 카테고리 API

#### 카테고리 생성 권한 체크
```
GET /api/categories/check-access
Authorization: Bearer {jwtToken}

Response: 200 OK
{
    "hasAccess": true
}

Error Responses:
- 401 Unauthorized: 로그인 필요
- 403 Forbidden: ADMIN 권한 필요
```

#### 카테고리 생성
```
POST /api/categories
Authorization: Bearer {jwtToken}
Content-Type: application/json
@PreAuthorize: ROLE_ADMIN

Request Body:
{
    "category": "Toys"
}

Response: 201 Created
```

#### 카테고리 목록 조회
```
GET /api/categories
(인증 불필요)

Response: 200 OK
[
    {"categoryId": 1, "category": "Electronics"},
    {"categoryId": 2, "category": "Clothing"},
    ...
]
```

#### 카테고리 수정
```
PUT /api/categories/{categoryId}
Authorization: Bearer {jwtToken}
Content-Type: application/json
@PreAuthorize: ROLE_ADMIN

Request Body:
{
    "category": "Electronics & Gadgets"
}

Response: 204 No Content
```

#### 카테고리 삭제
```
DELETE /api/categories/{categoryId}
Authorization: Bearer {jwtToken}
@PreAuthorize: ROLE_ADMIN

Response: 204 No Content
```

---

### 🛒 장바구니 API

#### 장바구니 추가
```
POST /api/carts
Authorization: Bearer {jwtToken}
Content-Type: application/json
@PreAuthorize: ROLE_USER, ROLE_SELLER, ROLE_ADMIN

Request Body:
{
    "goodsId": 1,
    "amount": 2,
    "optionNumber": 0  // 옵션 인덱스 (없으면 null)
}

Response: 201 Created
```

#### 내 장바구니 조회
```
GET /api/carts?page=0&size=10
Authorization: Bearer {jwtToken}
@PreAuthorize: ROLE_USER, ROLE_SELLER, ROLE_ADMIN

Response: 200 OK
{
    "content": [
        {
            "cartId": 1,
            "goodsId": 1,
            "goodsName": "노트북",
            "price": 1500000,
            "amount": 2,
            "totalPrice": 3000000,
            "imageUrl": "http://localhost:8080/uploads/xxx.jpg",
            "optionNumber": 0,
            "option": {"key": "색상", "value": "블랙"}
        },
        ...
    ],
    "totalPages": 1,
    "totalElements": 3
}
```

#### 장바구니 수량 수정
```
PUT /api/carts/{cartId}
Authorization: Bearer {jwtToken}
Content-Type: application/json
@PreAuthorize: ROLE_USER, ROLE_SELLER, ROLE_ADMIN

Request Body:
{
    "amount": 5
}

Response: 204 No Content
```

#### 장바구니 삭제
```
DELETE /api/carts/{cartId}
Authorization: Bearer {jwtToken}
@PreAuthorize: ROLE_USER, ROLE_SELLER, ROLE_ADMIN

Response: 204 No Content
```

---

### 📝 주문 API

#### 주문번호 UUID 생성
```
GET /api/merchantId
(인증 불필요)

Response: 201 Created
{
    "merchantId": "550e8400-e29b-41d4-a716-446655440000"
}
```

#### 주문 생성
```
POST /api/orders
Authorization: Bearer {jwtToken}
Content-Type: application/json
@PreAuthorize: ROLE_USER, ROLE_SELLER, ROLE_ADMIN

Request Body:
{
    "merchantId": "550e8400-e29b-41d4-a716-446655440000",
    "totalPrice": 3000000,
    "cartIds": [1, 2, 3]  // 주문할 장바구니 아이템 ID 목록
}

Response: 201 Created

비즈니스 로직:
1. 장바구니 아이템 조회
2. 상품 가격 변경 여부 확인 (checkUpdateGoods)
3. 가격 변동 시 예외 발생
4. 주문 생성 및 OrderItem 생성 (상품명, 가격 반정규화)
5. 장바구니 아이템 삭제
```

#### 내 주문 목록 조회
```
GET /api/orders?page=0&size=10
Authorization: Bearer {jwtToken}
@PreAuthorize: ROLE_USER, ROLE_SELLER, ROLE_ADMIN

Response: 200 OK
[
    {
        "orderId": 1,
        "merchantId": "550e8400-e29b-41d4-a716-446655440000",
        "totalPrice": 3000000,
        "orderStatus": "ORDER_COMPLETE",
        "createdAt": "2025-11-09T10:00:00"
    },
    ...
]
```

#### 주문 상세 조회
```
GET /api/orders/{orderId}
Authorization: Bearer {jwtToken}
@PreAuthorize: ROLE_USER, ROLE_SELLER, ROLE_ADMIN

Response: 200 OK
{
    "orderId": 1,
    "merchantId": "550e8400-e29b-41d4-a716-446655440000",
    "totalPrice": 3000000,
    "orderStatus": "ORDER_COMPLETE",
    "createdAt": "2025-11-09T10:00:00",
    "orderItems": [
        {
            "orderItemId": 1,
            "goodsName": "노트북",      // 주문 당시 상품명
            "goodsPrice": 1500000,      // 주문 당시 가격
            "amount": 2,
            "totalPrice": 3000000
        },
        ...
    ]
}
```

#### 결제 취소
```
POST /api/payCancel
Authorization: Bearer {jwtToken}
Content-Type: application/json
@PreAuthorize: ROLE_USER, ROLE_SELLER, ROLE_ADMIN

Request Body:
{
    "orderId": 1
}

Response: 204 No Content
```

---

## 비즈니스 로직

### 1. 회원가입 & 로그인

#### 회원가입 플로우
```
1. 클라이언트: 회원가입 폼 입력
2. 로그인 ID 중복 확인 (POST /api/members/exist)
3. 역할 선택 (USER or SELLER) - ADMIN은 수동 승격만 가능
4. 회원가입 요청 (POST /api/members/signup)
5. 서버:
   - 비밀번호 BCrypt 암호화
   - RoleType Enum: "USER" → "ROLE_USER" 자동 변환 (@JsonCreator)
   - Member 엔티티 저장
   - deletedStatus = false (기본값)
6. 성공 시 로그인 페이지로 리다이렉트
```

#### 로그인 플로우
```
1. 클라이언트: 로그인 ID, 비밀번호 입력
2. POST /api/members/login
3. 서버:
   - 로그인 ID로 Member 조회
   - deletedStatus = false 확인
   - BCrypt.matches(입력 비밀번호, 저장된 비밀번호) 검증
   - JWT 토큰 생성 (claim: loginId, role)
   - 로그인 히스토리 저장 (AOP)
4. 클라이언트:
   - JWT 토큰을 localStorage에 저장
   - Axios 인터셉터에서 모든 요청 헤더에 자동 추가
   - 홈 페이지로 리다이렉트
```

#### 소프트 딜리트 (회원 탈퇴)
```
1. DELETE /api/members
2. 서버:
   - deletedStatus = true로 변경 (물리적 삭제 X)
   - 개인정보는 null로 변경 (GDPR 대응)
   - 하지만 Member ID는 유지 → 주문/리뷰 데이터 무결성 보장
3. 로그인 시 deletedStatus = true 회원은 로그인 불가
```

---

### 2. 상품 등록 & 조회

#### 상품 등록 플로우
```
1. 프론트엔드:
   - 권한 체크 (GET /api/goods/check-access)
   - 401 → 로그인 페이지
   - 403 → "SELLER 권한이 필요합니다" 알럿
   - 200 → 상품 등록 페이지 이동

2. 상품 등록 폼:
   - 상품명, 가격, 설명 입력
   - 카테고리 선택 (GET /api/categories)
   - 이미지 업로드 (다중 파일, 미리보기)
   - 옵션 추가 (key-value 쌍)

3. POST /api/goods (multipart/form-data)
4. 서버:
   - 인증 토큰에서 판매자(Member) 추출
   - 이미지 파일 저장 (LocalFileService)
     - 로컬: /uploads/{yyyyMMdd}/{UUID}_{원본파일명}
     - FileEntity 생성 (fileUrl: http://localhost:8080/uploads/...)
   - 옵션 JSON → List<OptionCreate> 변환
   - Goods 엔티티 저장 (Member, Category 연관관계 설정)
5. 성공 시 상품 목록 페이지로 리다이렉트
```

#### 상품 조회 최적화
```
- 페이징 처리: Pageable 파라미터 (page, size, sort)
- N+1 문제 해결:
  - @BatchSize(size = 100) 적용
  - 필요 시 Fetch Join 사용
- 대표 이미지: imageList[0].fileUrl 사용
- QueryDSL 동적 쿼리:
  - 키워드 검색: goodsName LIKE %keyword%
  - 가격 범위 검색: price BETWEEN minPrice AND maxPrice
```

---

### 3. 장바구니 & 주문

#### 장바구니 추가 플로우
```
1. 프론트엔드:
   - 홈/목록/상세 페이지에서 "장바구니 담기" 버튼 클릭
   - 상세 페이지: 수량 선택 가능
   - 목록/홈: 기본 수량 1

2. POST /api/carts
3. 서버:
   - 인증 토큰에서 Member 추출
   - 동일 상품 + 동일 옵션 중복 확인
   - 중복 시: 기존 장바구니 수량 증가
   - 중복 없음: 새 Cart 엔티티 생성
4. 성공 알럿: "장바구니에 상품이 추가되었습니다!"
```

#### 주문 생성 플로우 (핵심 비즈니스 로직)
```
1. 프론트엔드:
   - 장바구니 페이지에서 상품 선택
   - "주문하기" 버튼 클릭
   - 주문번호 UUID 생성 (GET /api/merchantId)

2. POST /api/orders
   Request: {
       merchantId: UUID,
       totalPrice: 계산된 총액,
       cartIds: [선택된 장바구니 ID들]
   }

3. 서버 비즈니스 로직:

   a. 장바구니 검증
      - 선택된 모든 장바구니 아이템 조회
      - 로그인 사용자의 장바구니인지 확인

   b. 실시간 가격 검증 (핵심!)
      - checkUpdateGoods API 호출
      - 장바구니에 담긴 시점 vs 주문 시점 가격 비교
      - 가격 변동 감지 시:
        → PRICE_CHANGED 예외 발생
        → 프론트엔드에 에러 메시지 전달
        → "상품 가격이 변경되었습니다. 장바구니를 다시 확인해주세요."

   c. 주문 생성
      - Order 엔티티 생성 (merchantId, totalPrice, orderStatus=ORDER_COMPLETE)
      - OrderItem 생성 (반정규화!)
        → goodsName = goods.getGoodsName()  # 주문 시점 상품명
        → goodsPrice = goods.getPrice()     # 주문 시점 가격
        → 나중에 상품이 삭제되어도 주문 내역에는 이름/가격 보존

   d. 장바구니 정리
      - 주문 완료된 장바구니 아이템 삭제

4. 성공 시 주문 완료 페이지로 이동
```

#### 반정규화 전략 (OrderItem)
```
문제:
- 판매자가 상품을 삭제하면 주문 내역에서 상품명/가격을 알 수 없음
- Goods 테이블 JOIN으로는 삭제된 상품 정보 복구 불가
- 매출 통계, 주문 내역 조회 시 문제 발생

해결:
- OrderItem 테이블에 goodsName, goodsPrice 필드 추가
- 주문 생성 시점에 상품 정보를 복사하여 저장
- Goods 테이블과는 별도로 주문 시점 스냅샷 보존
- 데이터 무결성 vs 정규화: 비즈니스 요구사항에 맞춰 정규화 완화
```

---

### 4. 리뷰 & 대댓글 시스템

#### 리뷰 작성 권한
```
조건:
1. 로그인 필수
2. 해당 상품을 구매한 이력이 있어야 함
   - Order → OrderItem → Goods 조인
   - member_id = 현재 사용자 && goods_id = 리뷰 대상 상품
3. 이미 리뷰를 작성했다면 작성 불가 (중복 리뷰 방지)

검증 로직:
- ReviewService.checkPurchaseHistory(memberId, goodsId)
- 미구매 시: NOT_PURCHASED 예외 발생
```

#### 대댓글 작성 권한
```
조건:
1. SELLER 또는 ADMIN 권한 필요
2. 본인이 등록한 상품의 리뷰에만 답변 가능
   - Review → Goods → Member
   - goods.member.id = 현재 사용자 ID

검증 로직:
- ReplyService.checkGoodsOwner(memberId, reviewId)
- 본인 상품 아니면: FORBIDDEN 예외 발생
```

---

### 5. 파일 관리 시스템

#### 로컬 파일 저장 (LocalFileService)
```
개발 환경:
- AWS S3 의존성 제거 (비용 절감)
- 로컬 디스크에 파일 저장
- 업로드 경로: /uploads/{yyyyMMdd}/{UUID}_{원본파일명}

업로드 플로우:
1. MultipartFile 수신
2. 파일명 중복 방지: UUID 생성
3. 날짜별 디렉토리 생성: 20251109/
4. 파일 저장: Files.copy(inputStream, targetPath)
5. FileEntity 생성:
   - fileName: 원본파일명
   - filePath: /uploads/20251109/uuid_filename.jpg
   - fileUrl: http://localhost:8080/uploads/20251109/uuid_filename.jpg

정적 리소스 서빙 (WebConfig):
@Override
public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler("/uploads/**")
            .addResourceLocations("file:uploads/");
}

삭제 플로우:
1. FileEntity 조회
2. 물리적 파일 삭제: Files.deleteIfExists(path)
3. FileEntity 삭제
```

#### AWS S3 파일 저장 (운영 환경 - 현재 중단)
```
운영 환경:
- Profile: prod
- AwsFileService 사용
- S3 버킷에 업로드
- fileUrl: https://s3.amazonaws.com/bucket-name/...

미래 전환 계획:
- LocalFileService → AwsFileService 전환
- Profile 기반 자동 선택
- 업로드 경로만 변경, 비즈니스 로직 동일
```

---

## 프론트엔드 구조

### React 컴포넌트 설계

#### Axios 인터셉터 (api/axios.js)
```javascript
const api = axios.create({
    baseURL: 'http://localhost:8080/api',
    headers: { 'Content-Type': 'application/json' }
});

// 요청 인터셉터: JWT 토큰 자동 추가
api.interceptors.request.use(config => {
    const token = localStorage.getItem('jwtToken');
    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
});

// 응답 인터셉터: 401 에러 시 자동 로그아웃
api.interceptors.response.use(
    response => response,
    error => {
        if (error.response?.status === 401) {
            localStorage.removeItem('jwtToken');
            window.location.href = '/login';
        }
        return Promise.reject(error);
    }
);
```

#### 주요 페이지 구조

**1. Home.js (홈 페이지)**
```
구성:
- Hero Section (환영 메시지, 이미지)
- Featured Products (최신 상품 8개)
  - 상품 카드: 이미지, 이름, 가격, 상세보기 버튼, 장바구니 버튼
- Categories (카테고리 목록)
- 상단 버튼: 카테고리 생성 (ADMIN), 상품 등록 (SELLER)

권한 체크:
- 카테고리 생성 버튼: GET /api/categories/check-access
- 상품 등록 버튼: GET /api/goods/check-access
- 401 → 로그인 페이지
- 403 → 알럿 ("권한이 필요합니다")
- 200 → 해당 페이지 이동
```

**2. GoodsList.js (상품 목록)**
```
기능:
- 상품 목록 조회 (페이징)
- 페이지네이션
- 장바구니 추가 (수량 1 고정)

상품 카드:
- 대표 이미지
- 상품명
- 가격 (₩ 표시)
- 버튼 그룹:
  - 상세보기 (Link to /goods/{goodsId})
  - 장바구니 아이콘 버튼
```

**3. GoodsDetail.js (상품 상세)**
```
구성:
- 이미지 갤러리
  - 메인 이미지 (500px 높이)
  - 썸네일 목록 (클릭 시 메인 이미지 변경)
- 상품 정보
  - 상품명
  - 카테고리 Badge
  - 가격 (₩ 표시)
  - 상품 설명
  - 판매자 정보
- 수량 선택
  - 버튼: - / 숫자 입력 / +
  - 최소 수량: 1
- 총 가격 계산 (가격 × 수량)
- 버튼:
  - 장바구니 담기 (장바구니 아이콘)
  - 목록으로 (뒤로가기)

에러 처리:
- 401: 로그인 필요 → /login
- 기타: 에러 메시지 Alert 표시
```

**4. GoodsCreate.js (상품 등록)**
```
폼 필드:
- 상품명 (필수)
- 가격 (필수, 숫자)
- 카테고리 선택 (필수, Select)
- 상품 설명 (선택, Textarea)
- 이미지 업로드 (필수, 다중 파일)
  - 미리보기: 선택된 이미지 썸네일 표시
  - 삭제: 각 이미지별 X 버튼
- 옵션 추가 (선택)
  - Key: 옵션명 (예: 색상)
  - Value: 옵션값 (예: 블랙)
  - 추가/삭제 버튼

검증:
- 필수 필드 체크
- 가격은 양수만
- 이미지 최소 1장

제출:
- FormData로 변환 (multipart/form-data)
- 옵션은 JSON 문자열로 변환
- POST /api/goods
```

**5. MyPage.js (마이페이지)**
```
표시 정보:
- 로그인 ID
- 이름
- 이메일
- 전화번호
- 우편번호
- 상세 주소
- 권한 (Badge: ADMIN=danger, SELLER=success, USER=primary)
- 가입일
- 최종 수정일

기능:
- 정보 조회만 (GET /api/members/me)
- 수정 기능은 향후 추가 예정
```

**6. Login.js / Signup.js**
```
Login:
- 로그인 ID, 비밀번호 입력
- POST /api/members/login
- 성공 시 JWT 토큰 localStorage 저장
- 홈으로 리다이렉트

Signup:
- 필수: 로그인 ID, 비밀번호, 이름, 이메일, 역할
- 선택: 전화번호, 우편번호, 상세주소
- 로그인 ID 중복 확인 (실시간)
- POST /api/members/signup
- 성공 시 로그인 페이지로 이동
```

**7. CategoryCreate.js (카테고리 생성)**
```
권한: ADMIN만 접근 가능
폼:
- 카테고리명 입력 (필수)
- POST /api/categories
성공 시 홈으로 이동
```

---

### React Router 설정 (App.js)
```javascript
<BrowserRouter>
  <Navbar />
  <Routes>
    <Route path="/" element={<Home />} />
    <Route path="/login" element={<Login />} />
    <Route path="/signup" element={<Signup />} />
    <Route path="/mypage" element={<MyPage />} />

    <Route path="/goods" element={<GoodsList />} />
    <Route path="/goods/:goodsId" element={<GoodsDetail />} />
    <Route path="/goods/create" element={<GoodsCreate />} />

    <Route path="/categories/create" element={<CategoryCreate />} />
  </Routes>
</BrowserRouter>
```

---

## 개발 환경 설정

### 로컬 개발 환경

#### application-local.yml
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/shopping_mall
    username: shopuser
    password: shop1234
    driver-class-name: com.mysql.cj.jdbc.Driver

  jpa:
    hibernate:
      ddl-auto: update  # 테이블 자동 생성/수정
    show-sql: true
    properties:
      hibernate:
        format_sql: true
        default_batch_fetch_size: 100

  servlet:
    multipart:
      max-file-size: 10MB
      max-request-size: 50MB

# 파일 저장 경로
file:
  upload-dir: uploads

# CORS 설정 (React dev server)
cors:
  allowed-origins: http://localhost:3000
```

#### docker-compose.yml
```yaml
version: '3.8'
services:
  mysql:
    image: mysql:8.0
    container_name: shopping_mall_mysql
    environment:
      MYSQL_ROOT_PASSWORD: root1234
      MYSQL_DATABASE: shopping_mall
      MYSQL_USER: shopuser
      MYSQL_PASSWORD: shop1234
      TZ: Asia/Seoul
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql

volumes:
  mysql_data:
```

#### 실행 방법
```bash
# 1. MySQL 시작
docker-compose up -d

# 2. 백엔드 실행
JAVA_HOME=/path/to/jdk-17 ./gradlew bootRun --args='--spring.profiles.active=local'

# 3. 프론트엔드 실행
cd frontend
npm install
npm start

# 4. 접속
Frontend: http://localhost:3000
Backend: http://localhost:8080
Swagger: http://localhost:8080/swagger-ui/index.html
```

---

### 초기 데이터 설정

#### 카테고리 자동 생성 (InitDb.java)
```java
@Component
@RequiredArgsConstructor
public class InitDb {
    private final CategoryRepository categoryRepository;

    @PostConstruct
    public void init() {
        if (categoryRepository.count() == 0) {
            String[] categories = {
                "Electronics", "Clothing", "Food", "Books",
                "Sports", "Beauty", "Home", "Furniture"
            };
            Arrays.stream(categories).forEach(name ->
                categoryRepository.save(new Category(name))
            );
        }
    }
}
```

#### 테스트 계정 생성 (수동)
```
회원가입을 통해 생성:

1. 일반 사용자 (USER)
   - loginId: user123
   - password: user1234
   - role: USER

2. 판매자 (SELLER)
   - loginId: seller123
   - password: seller1234
   - role: SELLER

3. 관리자 (ADMIN) - DB 직접 수정
   - loginId: admin123
   - password: admin1234
   - role: ADMIN (회원가입 후 DB에서 수동 변경)
```

---

## 향후 개발 계획

### 📋 Phase 1: 기본 기능 개선
- [ ] 장바구니 페이지 구현 (현재 API만 존재)
- [ ] 주문 페이지 구현 (주문 내역 조회, 주문 상세)
- [ ] 결제 시스템 연동 (PG사 API)
- [ ] 리뷰/대댓글 페이지 구현
- [ ] 마이페이지 정보 수정 기능
- [ ] 상품 수정 페이지 구현

### 🎨 Phase 2: UI/UX 개선
- [ ] 로딩 스피너 일관성 개선
- [ ] 에러 메시지 Toast 알림 통일
- [ ] 반응형 디자인 개선 (모바일 최적화)
- [ ] 이미지 Lazy Loading
- [ ] 무한 스크롤 (페이지네이션 대체 옵션)

### 🚀 Phase 3: 성능 최적화
- [ ] React Query 도입 (서버 상태 관리)
- [ ] Redis 캐싱 추가 (상품 목록, 카테고리)
- [ ] 이미지 CDN 적용
- [ ] 쿼리 성능 모니터링 및 최적화
- [ ] API 응답 압축 (Gzip)

### 🔐 Phase 4: 보안 강화
- [ ] HTTPS 적용
- [ ] CSRF 토큰 추가
- [ ] Rate Limiting (API 요청 제한)
- [ ] XSS 필터링 강화
- [ ] 비밀번호 정책 강화 (길이, 복잡도)

### 📊 Phase 5: 관리자 기능
- [ ] 관리자 대시보드 (매출 통계)
- [ ] 회원 관리 (목록, 검색, 권한 변경)
- [ ] 상품 승인 시스템 (신규 상품 검토)
- [ ] 신고 관리 (부적절한 리뷰, 상품)

### 🧪 Phase 6: 테스트 & 품질
- [ ] E2E 테스트 (Playwright, Cypress)
- [ ] 프론트엔드 단위 테스트 (Jest, React Testing Library)
- [ ] API 통합 테스트 커버리지 확대
- [ ] 성능 테스트 (JMeter, K6)

### ☁️ Phase 7: 인프라 & DevOps
- [ ] AWS 재배포 (EC2, RDS, S3)
- [ ] CI/CD 파이프라인 복구
- [ ] 모니터링 시스템 (Prometheus, Grafana)
- [ ] 로그 수집 (ELK Stack)
- [ ] 무중단 배포 (Blue-Green, Canary)

---

## 알려진 이슈 & 제약사항

### 현재 이슈
1. **BaseTimeEntity 오타**: `cratedAt` → `createdAt` (기존 데이터 호환성으로 유지)
2. **장바구니/주문 프론트엔드 미구현**: API는 완성, UI만 추가 필요
3. **이미지 최적화 부재**: 원본 이미지 그대로 저장 (리사이징 필요)
4. **검색 기능 제한적**: 키워드, 가격 범위만 지원 (필터링 확대 필요)

### 기술적 제약
1. **JWT 토큰 갱신 미구현**: 만료 시 재로그인 필요 (Refresh Token 도입 필요)
2. **동시성 제어 부재**: 재고 관리 시 동시 주문 충돌 가능성
3. **트랜잭션 격리 수준**: 기본값 사용 (비즈니스 로직에 맞춰 조정 필요)
4. **파일 삭제 정책**: 상품 삭제 시 파일 자동 삭제 (복구 불가)

### 운영 환경 이슈
1. **AWS 배포 중단**: 비용 문제로 임시 중단
2. **HTTPS 미적용**: 로컬 개발 환경만 운영
3. **백업 전략 부재**: 데이터베이스 백업 자동화 필요
4. **모니터링 부재**: 에러 추적, 성능 모니터링 시스템 없음

---

## 참고 자료

### 공식 문서
- [Spring Boot Documentation](https://docs.spring.io/spring-boot/docs/2.7.5/reference/html/)
- [Spring Security Reference](https://docs.spring.io/spring-security/reference/index.html)
- [React Documentation](https://react.dev/)
- [React Router](https://reactrouter.com/)

### 학습 자료 (개발자 블로그)
- [QueryDSL 동적 쿼리](https://josteady.tistory.com/850)
- [Spring Security + JWT](https://josteady.tistory.com/838)
- [JPA N+1 문제 해결](https://josteady.tistory.com/839)
- [Docker + CI/CD](https://josteady.tistory.com/831)

---

**마지막 업데이트**: 2025.11.09
**작성자**: 프로젝트 개발자
**버전**: 1.0.0
