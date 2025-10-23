# **OpenMarket ShoppingMall**

[![Java](https://img.shields.io/badge/Java-17-orange)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-2.7.5-brightgreen)](https://spring.io/projects/spring-boot)
[![Docker](https://img.shields.io/badge/Docker-Containerized-blue)](https://www.docker.com/)
[![AWS](https://img.shields.io/badge/AWS-EC2%20%7C%20RDS%20%7C%20S3-yellow)](https://aws.amazon.com/)
[![GitHub Actions](https://img.shields.io/badge/CI%2FCD-GitHub%20Actions-success)](https://github.com/features/actions)

## 📋 프로젝트 개요

**OpenMarket ShoppingMall**은 다양한 상품을 판매할 수 있는 **C2C(Consumer to Consumer) 방식의 오픈마켓** 플랫폼입니다.
실제 서비스되고 있는 스마트 스토어의 비즈니스 로직을 분석하고 모티브하여 설계된 **RESTful API 서버**입니다.

### 🎯 핵심 특징
- **다중 사용자 권한 관리**: USER, SELLER, ADMIN 역할 기반 접근 제어
- **완전한 쇼핑몰 플로우**: 회원가입부터 상품 등록, 주문, 결제까지 전체 프로세스 구현
- **확장 가능한 아키텍처**: MSA 환경을 고려한 JWT 기반 인증 시스템
- **실시간 가격 검증**: 장바구니-주문 전환 시 상품 가격 변경 감지
- **자동화된 배포**: CI/CD 파이프라인을 통한 무중단 배포 구현

### 📊 프로젝트 정보
- **개발 기간**: 2022.10.31 ~ 2023.01.19 (약 3개월)
- **수정 기간**: 2025.10.23 ~ 
- **참여 인원**: 1명 (개인 프로젝트)
- **배포 환경**: AWS EC2 + Docker + GitHub Actions (중단)
- **API 문서**: [Swagger UI](http://15.165.145.187:8080/swagger-ui/index.html) (중단)

### 📝 변경 이력
- **2025.10.23**: **Thymeleaf → React 완전 전환** (Frontend 아키텍처 변경)
- **2025.10.23**: React 프론트엔드 추가 (React 18, React Router v6, Bootstrap)
- **2025.10.23**: CORS 설정 추가 (React dev server 연동)
- **2025.10.23**: 로컬 개발 환경 구성 (application-local.yml, docker-compose)
- **2025.10.23**: 회원가입 API 필드 수정 (phoneNumber, zipcode/detailAddress 선택사항)
- **2025.10.23**: View 레이어 완전 제거 (Thymeleaf, static files, ViewController)
- **2025.10.23**: 회원가입 권한 선택 - List에서 단일 선택으로 변경 (RoleType)
- **2025.10.23**: H2 Database에서 MySQL Docker로 전환 (데이터 영구 저장)
- **2025.10.23**: Map 사용 제거 및 DTO 패턴 적용 (LoginIdCheckRequest)

## 🛠 기술 스택

### Frontend
![React](https://img.shields.io/badge/React-18-61DAFB?style=flat&logo=react&logoColor=black)
![React Router](https://img.shields.io/badge/React%20Router-v6-CA4245?style=flat&logo=react-router&logoColor=white)
![Bootstrap](https://img.shields.io/badge/Bootstrap-5-7952B3?style=flat&logo=bootstrap&logoColor=white)
![Axios](https://img.shields.io/badge/Axios-5A29E4?style=flat&logo=axios&logoColor=white)

### Backend
![Java](https://img.shields.io/badge/Java-17-ED8B00?style=flat&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-2.7.5-6DB33F?style=flat&logo=spring&logoColor=white)
![Spring Security](https://img.shields.io/badge/Spring%20Security-6DB33F?style=flat&logo=springsecurity&logoColor=white)
![Spring Data JPA](https://img.shields.io/badge/Spring%20Data%20JPA-6DB33F?style=flat&logo=spring&logoColor=white)
![QueryDSL](https://img.shields.io/badge/QueryDSL-5.0.0-blue?style=flat)

### Database & Storage
![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?style=flat&logo=mysql&logoColor=white)
![H2](https://img.shields.io/badge/H2-Database-blue?style=flat)
![AWS RDS](https://img.shields.io/badge/AWS%20RDS-MySQL-232F3E?style=flat&logo=amazon-aws&logoColor=white)
![AWS S3](https://img.shields.io/badge/AWS%20S3-232F3E?style=flat&logo=amazon-s3&logoColor=white)

### Security & Authentication
![JWT](https://img.shields.io/badge/JWT-000000?style=flat&logo=JSON%20web%20tokens&logoColor=white)
![AWS Secrets Manager](https://img.shields.io/badge/AWS%20Secrets%20Manager-232F3E?style=flat&logo=amazon-aws&logoColor=white)

### DevOps & Deployment
![Docker](https://img.shields.io/badge/Docker-2496ED?style=flat&logo=docker&logoColor=white)
![GitHub Actions](https://img.shields.io/badge/GitHub%20Actions-2088FF?style=flat&logo=github-actions&logoColor=white)
![AWS EC2](https://img.shields.io/badge/AWS%20EC2-232F3E?style=flat&logo=amazon-aws&logoColor=white)
![Docker Hub](https://img.shields.io/badge/Docker%20Hub-2496ED?style=flat&logo=docker&logoColor=white)

### Testing & Documentation
![JUnit 5](https://img.shields.io/badge/JUnit%205-25A162?style=flat&logo=junit5&logoColor=white)
![Mockito](https://img.shields.io/badge/Mockito-4.10.0-green?style=flat)
![Swagger](https://img.shields.io/badge/Swagger-85EA2D?style=flat&logo=swagger&logoColor=black)

### Monitoring & Performance
![Ehcache](https://img.shields.io/badge/Ehcache-3.8.0-orange?style=flat)
![AOP](https://img.shields.io/badge/Spring%20AOP-6DB33F?style=flat&logo=spring&logoColor=white)

### Build Tools
![Gradle](https://img.shields.io/badge/Gradle-02303A?style=flat&logo=gradle&logoColor=white)

### 상세 기술 스택
| 카테고리 | 기술 | 버전 | 용도 |
|---------|------|------|------|
| **Frontend** | React | 18 | UI 프레임워크 |
| **Router** | React Router | v6 | 클라이언트 사이드 라우팅 |
| **UI Library** | React Bootstrap | 2.x | UI 컴포넌트 |
| **HTTP Client** | Axios | - | API 통신 |
| **Language** | Java | 17 | 메인 개발 언어 |
| **Framework** | Spring Boot | 2.7.5 | 애플리케이션 프레임워크 |
| **ORM** | Spring Data JPA | 2.7.5 | 데이터 접근 계층 |
| **Query** | QueryDSL | 5.0.0 | 동적 쿼리 생성 |
| **Security** | Spring Security + JWT | 2.7.5 | 인증/인가 |
| **Database** | MySQL | 8.0 | 운영 데이터베이스 |
| **Database** | H2 | 1.4.200 | 테스트 데이터베이스 |
| **Cache** | Ehcache | 3.8.0 | 애플리케이션 캐시 |
| **File Storage** | AWS S3 | - | 상품 이미지 저장 |
| **Server** | AWS EC2 | Amazon Linux 2 | 애플리케이션 서버 |
| **CI/CD** | GitHub Actions | - | 자동 빌드/배포 |
| **Container** | Docker | - | 컨테이너화 |
| **Testing** | JUnit 5 + Mockito | 5.9.0 / 4.10.0 | 단위/통합 테스트 |


## 🏗 시스템 아키텍처

### 전체 아키텍처
<img src="https://user-images.githubusercontent.com/103364805/215643040-1af1f2a0-d74b-440d-a93a-4de727060bb1.png"  width="800" height="400">

### 아키텍처 설명
- **Client Layer**: REST API를 통한 클라이언트 통신
- **API Gateway**: Spring Boot 애플리케이션 서버
- **Business Logic**: 도메인별로 분리된 서비스 계층
- **Data Layer**: JPA/QueryDSL을 통한 데이터 접근
- **Infrastructure**: AWS 클라우드 기반 인프라스트럭처

### 배포 아키텍처
```
GitHub Repository
       ↓ (Push/PR)
GitHub Actions (CI/CD)
       ↓ (Build & Test)
Docker Hub (Image Registry)
       ↓ (Deploy)
AWS EC2 (Application Server)
       ↓ (Data)
AWS RDS (MySQL Database)
       ↓ (File Storage)
AWS S3 (Image Storage)
```

## 📁 프로젝트 구조

<img src="https://user-images.githubusercontent.com/103364805/213636905-b65f085a-060d-4cab-a289-14d7b3529d79.png"  width="300" height="600">

### 패키지 구조
```
src/main/java/com/project/shop/
├── global/                     # 공통 설정 및 유틸리티
│   ├── config/                # 설정 클래스들
│   │   ├── security/          # Spring Security 설정
│   │   ├── swagger/           # Swagger 설정
│   │   └── cache/             # Ehcache 설정
│   ├── exception/             # 예외 처리
│   └── util/                  # 유틸리티 클래스들
├── member/                    # 회원 도메인
│   ├── domain/               # 엔티티 클래스
│   ├── controller/           # REST Controller
│   ├── service/              # 비즈니스 로직
│   └── repository/           # 데이터 접근 계층
├── goods/                    # 상품 도메인
│   ├── domain/               # 상품, 카테고리, 리뷰 엔티티
│   ├── controller/           # 상품 관련 API
│   ├── service/              # 상품 비즈니스 로직
│   └── repository/           # 상품 데이터 접근
└── order/                    # 주문 도메인
    ├── domain/               # 주문, 결제 엔티티
    ├── controller/           # 주문 관련 API
    ├── service/              # 주문 비즈니스 로직
    └── repository/           # 주문 데이터 접근
```

## 🗄 데이터베이스 설계 (ERD)

### 메인 ERD
<img src="https://user-images.githubusercontent.com/103364805/213637190-8ed2c18a-d1c6-4768-a1d8-650a5b696f86.png"  width="800" height="500">

### 확장 ERD
<img src="https://user-images.githubusercontent.com/103364805/213637232-d6de2126-797f-47e5-ad5b-efe563cd1d0f.png"  width="800" height="500">

### 주요 테이블 관계
- **Member ↔ Cart**: 1:N (회원 당 여러 장바구니 아이템)
- **Member ↔ Order**: 1:N (회원 당 여러 주문)
- **Member ↔ Goods**: 1:N (판매자 당 여러 상품)
- **Goods ↔ Category**: N:1 (상품 당 하나의 카테고리)
- **Order ↔ OrderItem**: 1:N (주문 당 여러 주문 아이템)
- **Goods ↔ Review**: 1:N (상품 당 여러 리뷰)
- **Review ↔ Reply**: 1:1 (리뷰 당 하나의 대댓글)


## 🔧 주요 기능

### 🎯 핵심 비즈니스 로직
- **다중 권한 시스템**: USER(구매자), SELLER(판매자), ADMIN(관리자) 역할별 접근 제어
- **실시간 가격 검증**: 장바구니에서 주문으로 넘어갈 때 상품 가격 변경 여부 확인
- **소프트 딜리트**: 회원 탈퇴 시 데이터 무결성 보장을 위한 논리적 삭제
- **이미지 관리**: AWS S3를 통한 상품 이미지 업로드/다운로드/삭제
- **캐싱 시스템**: Ehcache를 통한 성능 최적화

### 📋 API 엔드포인트 목록

#### 👤 회원 관리 (`/api/members`)
- `POST /signup` - 회원가입
- `POST /exist` - 아이디 중복 확인
- `POST /login` - 로그인 (JWT 토큰 발급)
- `GET /me` - 내 정보 조회
- `PUT /` - 회원 정보 수정
- `DELETE /` - 회원 탈퇴

#### 🛍 상품 관리 (`/api/goods`)
- `POST /` - 상품 등록 (이미지 업로드 포함)
- `GET /` - 상품 전체 조회 (페이징)
- `GET /{goodsId}` - 상품 상세 조회
- `GET /keyword` - 상품 키워드 검색
- `GET /search` - 가격 범위별 상품 검색
- `GET /checkUpdateGoods` - 상품 가격 변경 확인
- `POST /{goodsId}` - 상품 수정
- `DELETE /{goodsId}` - 상품 삭제

#### 📦 카테고리 관리 (`/api/categories`)
- `POST /` - 카테고리 생성
- `GET /` - 카테고리 전체 조회
- `PUT /{categoryId}` - 카테고리 수정
- `DELETE /{categoryId}` - 카테고리 삭제

#### 🛒 장바구니 관리 (`/api/carts`)
- `POST /` - 장바구니 상품 추가
- `GET /` - 내 장바구니 조회
- `PUT /{cartId}` - 장바구니 수량 수정
- `DELETE /{cartId}` - 장바구니 상품 삭제

#### 📝 주문 관리 (`/api/orders`)
- `GET /merchantId` - 주문번호 UUID 생성
- `POST /` - 주문 생성
- `GET /` - 내 주문 내역 조회
- `GET /{orderId}` - 주문 상세 조회
- `POST /payCancel` - 결제 취소

#### ⭐ 리뷰 관리 (`/api/reviews`)
- `POST /` - 리뷰 작성 (구매한 상품만)
- `GET /goods/{goodsId}` - 상품별 리뷰 조회
- `PUT /{reviewId}` - 리뷰 수정
- `DELETE /{reviewId}` - 리뷰 삭제

#### 💬 대댓글 관리 (`/api/replies`)
- `POST /` - 대댓글 작성 (판매자만)
- `GET /review/{reviewId}` - 리뷰별 대댓글 조회
- `PUT /{replyId}` - 대댓글 수정
- `DELETE /{replyId}` - 대댓글 삭제

### 🔐 권한별 접근 제어
| 기능 | USER | SELLER | ADMIN |
|------|------|--------|-------|
| 상품 등록/수정/삭제 | ❌ | ✅ | ✅ |
| 상품 조회/검색 | ✅ | ✅ | ✅ |
| 장바구니 관리 | ✅ | ✅ | ✅ |
| 주문 생성/조회 | ✅ | ❌ | ✅ |
| 리뷰 작성/수정/삭제 | ✅ (본인만) | ❌ | ✅ |
| 대댓글 작성/수정 | ❌ | ✅ (본인 상품만) | ✅ |
| 카테고리 관리 | ❌ | ❌ | ✅ |

더 자세한 기능별 시퀀스 다이어그램은 [여기](https://resolute-meeting-a79.notion.site/Sequence-Diagram-f743df1a9a2543ecaf90b536e0b4a81d)에서 확인할 수 있습니다.

## 🚀 시작하기

### 📋 필수 조건
- Java 17 이상
- Docker & Docker Compose
- AWS 계정 (S3, RDS, EC2 - 선택사항)

### 🛠 로컬 개발 환경 설정

#### 1. 프로젝트 클론
```bash
git clone [repository-url]
cd shoppingmall_project
```

#### 2. 데이터베이스 설정 (Docker Compose)
```bash
# MySQL Docker 컨테이너 실행
docker-compose up -d

# 확인
docker ps
```

#### 3. 백엔드 실행 (Spring Boot)
```bash
# 로컬 프로필로 실행
JAVA_HOME=/path/to/jdk-17 ./gradlew bootRun --args='--spring.profiles.active=local'

# 또는 빌드 후 실행
./gradlew clean build -x test
java -jar build/libs/*.jar --spring.profiles.active=local
```

#### 4. 프론트엔드 실행 (React)
```bash
cd frontend
npm install
npm start
```

#### 5. 접속 확인
- **Frontend (React)**: http://localhost:3000
- **Backend API**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/swagger-ui/index.html
- **MySQL**: localhost:3306 (user: shopuser, password: shop1234)

#### 6. 초기 데이터
```sql
-- 카테고리 자동 생성됨 (8개)
-- Electronics, Clothing, Food, Books, Sports, Beauty, Home, Furniture
```

### 🐳 Docker로 실행
```bash
# Docker 이미지 빌드
docker build -t shopping-mall .

# 컨테이너 실행
docker run -p 8080:8080 \
  -e spring.datasource.url=jdbc:mysql://host.docker.internal:3306/shopping_mall \
  -e spring.datasource.username=root \
  -e spring.datasource.password=password \
  shopping-mall
```

### 🧪 테스트 실행
```bash
# 단위 테스트 실행
./gradlew test

# 통합 테스트 포함 전체 테스트
./gradlew test --tests "*ControllerTest*"
```

## 📊 성능 및 모니터링

### 🔧 성능 최적화 기법
- **Ehcache 적용**: 자주 조회되는 카테고리 및 상품 정보 캐싱
- **QueryDSL 동적 쿼리**: 복잡한 검색 조건에 대한 효율적인 쿼리 생성
- **페이징 처리**: 대용량 데이터 조회 시 메모리 사용량 최적화
- **N+1 문제 해결**: 지연 로딩과 페치 조인을 통한 쿼리 최적화
- **배치 사이즈 설정**: `default_batch_fetch_size: 100`으로 IN 쿼리 최적화

### 📈 AOP 기반 모니터링
- **실행 시간 측정**: `@TimeAop` 어노테이션을 통한 메서드 실행 시간 측정
- **로그인 이력 관리**: 사용자 로그인 패턴 분석을 위한 히스토리 데이터 수집
- **자동 데이터 정리**: Spring Scheduler를 통한 일일 로그인 히스토리 초기화

## 🎯 시퀀스 다이어그램
각 비즈니스 플로우의 상세한 동작 과정을 시각화했습니다.

📖 **[상세 시퀀스 다이어그램 보러가기](https://resolute-meeting-a79.notion.site/Sequence-Diagram-f743df1a9a2543ecaf90b536e0b4a81d)**

## ✅ 테스트 전략

### 🧪 테스트 커버리지
<img src="https://user-images.githubusercontent.com/103364805/213638251-372cc0b6-2847-41b2-90eb-bfc832df181f.png"  width="700" height="500">

### 📋 테스트 구조
- **단위 테스트 (Service Layer)**: 비즈니스 로직의 정확성 검증
- **통합 테스트 (Controller Layer)**: API 엔드포인트의 전체 플로우 검증
- **Mock 테스트**: Mockito를 활용한 외부 의존성 격리
- **CI/CD 테스트**: GitHub Actions에서 자동 테스트 실행

### 🎯 테스트 전략
```java
// 서비스 단위 테스트 예시
@ExtendWith(MockitoExtension.class)
class MemberServiceImplTest {
    @Mock MemberRepository memberRepository;
    @InjectMocks MemberServiceImpl memberService;

    @Test
    void 회원가입_성공() {
        // given, when, then 패턴으로 테스트 작성
    }
}

// 컨트롤러 통합 테스트 예시
@SpringBootTest
@AutoConfigureTestDatabase
class MemberControllerTest {
    @Test
    void 회원가입_API_성공() {
        // 실제 HTTP 요청을 통한 엔드투엔드 테스트
    }
}
```

## 💡 기술적 도전과 해결

### 🔐 **인증 & 보안 시스템 구축**
- **JWT 기반 인증**: 세션 방식 대비 확장성과 성능 향상을 위한 무상태 인증 구현
- **동적 Secret Key**: 서버 재시작 시마다 자동 생성되는 JWT Secret Key로 보안 강화
- **다중 권한 시스템**: `@PreAuthorize`를 활용한 메서드 레벨 권한 제어
- **AWS Secrets Manager**: 민감한 설정 정보의 안전한 외부 관리

### ⚡ **성능 최적화 & 모니터링**
- **AOP 성능 모니터링**: 커스텀 `@TimeAop` 어노테이션으로 메서드 실행 시간 측정
- **스케줄러 기반 데이터 관리**: Spring Scheduler로 로그인 히스토리 일일 정리
- **캐싱 전략**: Ehcache 3.x를 활용한 조회 성능 향상
- **QueryDSL 동적 쿼리**: 복합 검색 조건에 대한 효율적인 쿼리 생성

### 🛡 **데이터 무결성 & 안정성**
- **소프트 딜리트**: 데이터 추적성을 위한 논리적 삭제 구현
- **트랜잭션 관리**: `@Transactional`을 통한 데이터 일관성 보장
- **테이블 반정규화**: 성능과 데이터 무결성 간의 트레이드오프 최적화
- **실시간 가격 검증**: 주문 시점 상품 가격 변경 감지 및 처리

### ☁️ **클라우드 아키텍처 & DevOps**
- **AWS 인프라**: EC2, RDS, S3를 활용한 확장 가능한 클라우드 아키텍처
- **컨테이너화**: Docker를 통한 환경 독립적 배포
- **CI/CD 파이프라인**: GitHub Actions로 자동 빌드/테스트/배포
- **무중단 배포**: 컨테이너 기반 롤링 배포 전략

### 🧪 **테스트 주도 개발**
- **포괄적 테스트**: 단위/통합 테스트를 통한 99% 이상 커버리지 달성
- **Mock 기반 테스트**: Mockito를 활용한 외부 의존성 격리
- **CI 테스트 통합**: 배포 전 자동 테스트 실행으로 품질 보장

## 🛠 트러블슈팅

### 🗃 **데이터 무결성 문제**
**📋 문제상황**
- 회원 탈퇴 시 연관된 모든 데이터(상품, 리뷰, 대댓글 등)를 삭제하면 판매 통계 및 비즈니스 분석에 필요한 데이터가 소실됨

**💡 해결방안**
- **OrderItem 테이블 반정규화** 구현
- 상품명, 상품가격 필드를 OrderItem에 추가하여 주문 시점 정보 보존
- 회원 정보는 소프트 딜리트로 처리하여 개인정보는 삭제하되 비즈니스 데이터는 유지

```sql
-- 반정규화 적용 예시
ORDER_ITEM Table:
- goods_id (FK)
- goods_name (추가)
- goods_price (추가)
- order_date
```

### 🔧 **상품 옵션 데이터 구조 개선**
**📋 문제상황**
- 초기 `Map<String, Object>` 구조로 인한 타입 안정성 부족
- 런타임에서 데이터 추출 시 ClassCastException 발생 가능성

**💡 해결방안**
- **타입 안전한 `List<OptionCreate>` 구조**로 변경
- JPA Converter를 활용한 JSON 직렬화/역직렬화
- Key-Value 기반 명확한 옵션 데이터 관리

<img src="https://user-images.githubusercontent.com/103364805/213638733-a2516fb5-abdd-4ed6-9213-35cce916f8be.png"  width="800" height="550">

```java
// 개선된 옵션 구조
@Entity
public class GoodsOption {
    @Convert(converter = OptionListConverter.class)
    private List<OptionCreate> options;
}
```

### 🔐 **인증 시스템 아키텍처 결정**
**📋 문제상황**
- 세션 방식 vs JWT 방식 선택 이슈
- 확장성과 성능, MSA 환경 고려 필요

**💡 해결방안**
- **JWT 토큰 기반 인증** 채택
- 무상태(Stateless) 특성으로 서버 부하 감소
- MSA 환경에서의 서비스 간 인증 용이성 확보

<img src="https://user-images.githubusercontent.com/103364805/215916194-ea5148f0-eff6-427d-94db-cd4f4fc7e74c.png" width="500" height="500">

### 🔑 **JWT Secret Key 보안 강화**
**📋 문제상황**
- 고정된 Secret Key로 인한 보안 취약성
- Key 노출 시 수동 변경 및 재배포 필요

**💡 해결방안**
- **동적 Secret Key 생성** 알고리즘 구현
- 서버 재시작마다 자동 키 갱신
- 우아한 형제들 코프링 레퍼런스 참조

<img src="https://user-images.githubusercontent.com/103364805/213639070-6fe6d733-4e8c-40f5-b2e9-d43451eb559e.png" width="800" height="60">

### 📝 **API 응답 데이터 최적화**
**📋 문제상황**
- Entity 직접 노출로 인한 불필요한 데이터 전송
- 순환 참조 및 성능 이슈 발생

**💡 해결방안**
- **전용 Response DTO** 설계 및 구현
- 필요한 데이터만 선별적 노출
- API 응답 속도 향상 및 보안 강화

<img src="https://user-images.githubusercontent.com/103364805/213639306-dea7793a-1ad5-4aeb-92c3-fa5c2ade8b62.png" width="800" height="300">

### 🐳 **CI/CD 배포 네트워크 이슈**
**📋 문제상황**
- GitHub Actions에서 Docker Hub 이미지 pull 시 I/O timeout 에러
- 네트워크 연결 문제로 배포 파이프라인 중단

**💡 해결방안**
- **AWS EC2 보안 그룹** 설정 최적화
- SSH 포트(22) 접근 범위를 적절히 조정
- 방화벽 설정 검토 및 네트워크 정책 개선

<img src="https://user-images.githubusercontent.com/103364805/213639607-f0453880-59f0-41a0-9dc1-2a0207176844.png" width="700" height="700">
## 📚 학습 및 성장 기록

### 💡 **핵심 학습 성과**
이 프로젝트를 통해 학습하고 적용한 기술들과 관련 에러 해결 과정을 체계적으로 정리했습니다.
실제 개발 과정에서 겪은 문제들과 해결 방법을 문서화하여 향후 유사한 상황에서 효율적으로 대응할 수 있도록 했습니다.

### 🎓 **기술 학습 아카이브**

#### 🔍 **데이터베이스 & ORM**
- [QueryDSL 다중 조건 검색 구현](https://josteady.tistory.com/850)
- [List → Page 타입 리팩토링 전략](https://josteady.tistory.com/842)
- [OSIV(Open Session In View) 최적화](https://josteady.tistory.com/840)
- [JPA N+1 문제 해결 방법](https://josteady.tistory.com/839)
- [JPA 순환 참조 해결 전략](https://josteady.tistory.com/776)

#### 🔐 **보안 & 인증**
- [Spring Security + JWT 인증 구현](https://josteady.tistory.com/838)
- [Swagger JWT 토큰 연동](https://josteady.tistory.com/794)
- [AWS Secrets Manager Spring Boot 연동](https://josteady.tistory.com/830)

#### ☁️ **클라우드 & DevOps**
- [Spring Boot + Docker + GitHub Actions CI/CD](https://josteady.tistory.com/831)
- [AWS EC2 + Docker + MySQL 배포](https://josteady.tistory.com/828)
- [Docker Compose를 활용한 개발 환경 구성](https://josteady.tistory.com/826)
- [RDS MySQL 연결 및 타임존 설정](https://josteady.tistory.com/829)

#### 🎯 **성능 최적화**
- [Ehcache 2.x → 3.x 마이그레이션](https://josteady.tistory.com/811)
- [Ehcache 캐싱 전략 구현](https://josteady.tistory.com/808)
- [Spring AOP 성능 모니터링](https://josteady.tistory.com/810)
- [Spring Scheduler 배치 작업](https://josteady.tistory.com/812)

#### 🧪 **테스트 & 품질**
- [Page 객체 테스트 코드 작성](https://josteady.tistory.com/799)
- [MultipartFile 통합 테스트](https://josteady.tistory.com/814)
- [Spring Boot 특정 테스트 제외](https://josteady.tistory.com/833)

### 🚨 **에러 해결 아카이브**

#### ⚙️ **환경 설정 오류**
- [ApplicationContext 로딩 실패](https://josteady.tistory.com/836)
- [Docker Hub 접근 권한 거부](https://josteady.tistory.com/822)
- [포트 중복 사용 오류](https://josteady.tistory.com/818)

#### ☁️ **AWS 관련 오류**
- [AWS Access Key 인증 오류](https://josteady.tistory.com/815)
- [S3 접근 권한 거부](https://josteady.tistory.com/793)

#### 🗃 **JPA & 데이터베이스 오류**
- [Unsaved Transient Instance 오류](https://josteady.tistory.com/806)
- [Query Unique Result 오류](https://josteady.tistory.com/791)
- [참조 무결성 제약 조건 위반](https://josteady.tistory.com/773)
- [JPA Metamodel Empty 오류](https://josteady.tistory.com/767)

#### 🔧 **기타 기술적 오류**
- [Jackson 직렬화 오류](https://josteady.tistory.com/760)
- [Validation 타입 불일치](https://josteady.tistory.com/725)
- [Swagger 설정 오류](https://josteady.tistory.com/768)

## 🎯 프로젝트 회고 및 성장

### 💭 **개발자로서의 성장**

#### 🔍 **기술적 역량 향상**
- **아키텍처 설계 능력**: 단순한 CRUD를 넘어 확장 가능한 시스템 아키텍처 설계 경험
- **문제 해결 능력**: 다양한 기술적 이슈를 스스로 해결하며 troubleshooting 역량 강화
- **코드 품질 의식**: 테스트 주도 개발과 리팩토링을 통한 코드 품질 개선 의식 확립

#### 🎓 **학습과 적용의 선순환**
- **지속적 학습**: 모르는 기술을 끈질기게 학습하여 실제 프로젝트에 적용하는 경험
- **문서화 습관**: 학습한 내용을 체계적으로 정리하여 지식 자산화
- **오픈소스 활용**: 우아한 형제들 등 오픈소스 레퍼런스를 분석하고 프로젝트에 적용

### 💡 **핵심 깨달음**

#### 🚀 **"동작하는 코드"를 넘어서**
> *"기능이 정상적으로 동작하는 것은 끝이 아닌 시작점"*

- 단순히 작동하는 코드가 아닌, **유지보수 가능하고 확장 가능한 코드**의 중요성 인식
- 성능, 보안, 가독성을 모두 고려한 **종합적인 코드 품질** 추구

#### 🧪 **테스트의 가치**
> *"테스트하기 쉬운 코드가 좋은 코드"*

- 단위 테스트와 통합 테스트를 통한 **코드 신뢰성** 확보
- 리팩토링 시 테스트 코드가 주는 **안정감과 자신감** 경험

#### 🔄 **실패를 통한 성장**
> *"시행착오를 통해 더 많이 배우고 성장"*

- 에러와 실패를 두려워하지 않고 **적극적으로 도전**하는 자세 형성
- 각 에러를 해결하며 얻은 **깊이 있는 이해**의 가치 인식

### 🔄 **지속적 개선 계획**

#### 📈 **기술적 발전 방향**
- **마이크로서비스 아키텍처** 적용 경험
- **대용량 트래픽 처리** 및 성능 최적화
- **모니터링 시스템** 구축 (ELK Stack, Prometheus 등)

#### 🤝 **협업 능력 강화**
- **코드 리뷰 문화** 경험 및 개선
- **애자일 개발 방법론** 적용
- **기술 공유 및 멘토링** 활동 참여

## 🌟 마치며

이 프로젝트는 단순한 **쇼핑몰 API 개발**을 넘어, **실무에서 요구되는 종합적인 개발 역량**을 기를 수 있었던 소중한 경험이었습니다.

특히 **개발 → 테스트 → 배포 → 모니터링**의 전체 라이프사이클을 경험하며,
단순히 코드를 작성하는 것이 아닌 **지속 가능한 소프트웨어를 만드는 것**의 의미를 깨달았습니다.

앞으로도 이런 **체계적이고 품질 높은 개발 문화**를 바탕으로 더 나은 개발자로 성장해 나가겠습니다.

---

### 📞 Contact
- **Email**: [개발자 이메일]
- **Blog**: [기술 블로그](https://josteady.tistory.com/)
- **GitHub**: [GitHub 프로필]

**⭐ 이 프로젝트가 도움이 되셨다면 Star를 눌러주세요!**
