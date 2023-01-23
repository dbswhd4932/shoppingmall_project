# **OpenMarket shoppingMall**
- 다양한 상품을 판매 할 수 있는 오픈마켓(C2C 방식) 쇼핑몰 API 입니다.
- 인터넷 쇼핑을 할 때 주로 사용하는 스마트 스토어를 모티브하여 설계했습니다.
- 개발 기간 : 22.10.31 ~ 23.01.19<br>
- 참여 인원 : 1명<br>

- Swagger 문서는 여기서 확인할 수 있습니다 ->
[Swagger 문서 확인하러 가기](http://15.165.145.187:8080/swagger-ui/index.html)

# 기술 스택
- Language : Java<br>
- Framework : Spring 5.3 / Springboot 2.7.5<br>
- JDK : 17<br>
- BuildTool : Gradle<br>
- DB : H2, MySQL , RDS , S3<br>
- Server : AWS EC2 , Linux <br>
- CI / CD : Github Actions , Docker, Docker Hub<br>


# 아키텍처
<img src="https://user-images.githubusercontent.com/103364805/213636580-3d250d2a-df37-4613-850f-e3caa0a0a6a6.png"  width="650" height="330">

# 프로젝트 구조
<img src="https://user-images.githubusercontent.com/103364805/213636905-b65f085a-060d-4cab-a289-14d7b3529d79.png"  width="260" height="530">

# ERD
<img src="https://user-images.githubusercontent.com/103364805/213637190-8ed2c18a-d1c6-4768-a1d8-650a5b696f86.png"  width="800" height="470">
<img src="https://user-images.githubusercontent.com/103364805/213637232-d6de2126-797f-47e5-ad5b-efe563cd1d0f.png"  width="800" height="470">


# 기능설명
개발자가 아닌, 누구나 어떤 기능이 있는지 확인할 수 있도록 간단히 작성했습니다.<br>
기능에 대한 상세내용은 아래의 시퀀스 다이어그램을 확인부탁드리겠습니다.

<details>
<summary>회원</summary>
 
- 사이트를 통해 회원 가입 및 로그인
- 소셜 로그인(카카오) 인증 후 로그인

    +프론트에서 로그인 소셜 타입을 전달받아 사용 EX ) NO_SOCIAL / KAKAO 

- 로그인 시, 로그인 기록 저장 ( 매일 자정에 데이터 초기화 )
- 아이디 중복 체크
- 회원 정보 확인
- 회원 정보 수정
- 회원 탈퇴
    
    +공통 : 탈퇴 시, DB 에서 삭제되지 않으며 DeleteAt 필드 값이 현재 시간으로 초기화
    
    +USER : 회원이 가지고 있는 장바구니 데이터 삭제
    
    +SELLER : 등록한 상품에 연관된 데이터 삭제
    
    +ADMIN : 해당 없음
</details>


<details>
<summary>카테고리</summary>
 
- 카테고리 생성
- 카테고리 조회
- 카테고리 수정
- 카테고리 삭제
</details>


<details>
<summary>상품</summary>
 
- 상품 등록
    
    +상품 이미지는 1장 이상 필수로 입력
    
    +상품 이미지는 AWS S3 에 저장
    
    +동일한 이름으로는 등록 불가
    
- 상품 전체 조회
- 상품 단품 조회
- 상품 키워드 검색
- 상품 수정
- 상품 삭제
- 상품 가격 변경 확인
    
    +장바구니에서 주문 화면으로 넘어가기 전 가격 일치 여부 확인 (상품 가격 수정 가능성)
</details>


<details>
<summary>장바구니</summary>
 
- 장바구니 상품 추가
- 장바구니 조회
- 장바구니 수정
- 장바구니 삭제
</details>


<details>
<summary>주문</summary>
 
- 주문 번호 ID 생성 (UUID - 고유번호)
- 주문 생성
- 주문 전체 조회
- 주문 단건 조회
- 결제 취소
</details>


<details>
<summary>리뷰</summary>
 
- 리뷰 생성
    
    +자신이 구매한 상품만 리뷰 생성 가능
    
- 리뷰 조회
- 리뷰 수정
- 리뷰 삭제
    
    +구매자 뿐만 아니라 ADMIN 도 삭제 가능
</details>


<details>
<summary>대댓글</summary>
 
- 대댓글 생성
    
    +상품 판매자만 대댓글 작성 가능
    
- 대댓글 조회
- 대댓글 수정
- 대댓글 삭제
    
    +판매자 뿐만 아니라 ADMIN 도 삭제 가능
</details>


# 시퀀스 다이어그램
각 서비스마다 자세히 flow 를 나타내기 위해 작성했습니다.<br>
시퀀스 다이어그램은 여기서 확인할 수 있습니다 ->
[시퀀스 다이어그램](https://resolute-meeting-a79.notion.site/Sequence-Diagram-f743df1a9a2543ecaf90b536e0b4a81d)

# 테스트 진행 여부
Service 테스트는 단위테스트로 작성했으며, Controller 테스트는 통합테스트로 작성했습니다.<br>
<img src="https://user-images.githubusercontent.com/103364805/213638251-372cc0b6-2847-41b2-90eb-bfc832df181f.png"  width="610" height="470">

# 기술적 도전

- JWT 토큰을 통해 로그인을 구현하며, 권한 부여를 통해 메서드에 접근할 수 있는 사용자를 제어
- AOP 어노테이션을 구현하여 특정 메서드 실행시간 측정 및 로그인 시 히스토리 테이블에 입력
- Spring Sechduled 를 이용하여 로그인 히스토리 테이블 자정마다 테이블 초기화
- @SpringbootTest 및 Mockito 테스트를 통한 작동 검증 및 리팩토링 용이
- AWS S3 를 이용한 상품 이미지를 관리 ( 등록, 수정, 삭제, 다운로드 등 )
- AWS Secret Manager 를 통한 DB, AWS 중요 데이터는 외부 주입을 통해 사용
- Docker Hub 를 사용하여 OS 관계없이 동일한 환경 제공
- GitHub Actions 를 이용하여 설정 브랜치에 푸시 , PR 행위가 발생할 경우 workflows 를 따라 자동 배포 진행 + 테스트 코드가 실패할 경우 에러가 발생해 배포 불가
- 테이블 반정규화를 통한 트레이드오프 진행

# 트러블 슈팅

- 회원 탈퇴 시, 회원이 등록한 상품에 관련된 데이터 (상품, 리뷰, 대댓글, 옵션, 이미지 등) 이 모두 삭제되면 상품에 대한 판매량 등 데이터 취합에 관련해 문제가 생길 것으로 판단했습니다.<br>
→ Order_Item 테이블을 반정규화하여 상품명, 상품가격 필드값을 추가하여 탈퇴한 회원에 대한 불필요한 정보는 삭제하고 트레이드오프를 통해 필요한 정보는 남겨둘 수 있습니다.

- 상품 옵션관련해 Map<String, Object> 타입으로 구현했으나, 이후 옵션으로 데이터를 가공할 시 꺼내어 사용할 수 없는 문제가 생길 것이라고 판단했습니다.<br>
→옵션이 n개가 입력될 수 있기 때문에 List<OptionCreate> 형태로 변경하였습니다.
 key, value 값을 입력받아 사용하도록 변경하여 옵션 타입을 명시하고 key 값으로 데이터 조회가 가능합니다.
JSON 문자열 형태로 저장하기 위해서 JPA Converter 를 사용했습니다.
<img src="https://user-images.githubusercontent.com/103364805/213638733-a2516fb5-abdd-4ed6-9213-35cce916f8be.png"  width="760" height="530">

- 로그인을 비교적 간단한 세션방식으로 구현하려했으나 트래픽이 많아질 시 ,서버에 부하가 많이 일어날 것으로 예상했으며,  JWT 로 마이그레이션 한다고해도 그에 대한 비용이 상당하다고 판단했습니다.<br>
→ 다양한 도메인에 쉽게 접근할 수 있고 이후 MSA 환경을 고려해 JWT 방식으로 사용했습니다.

- JWT 관련하여 yml에 secret 키가 노출되었을 경우, 직접 수동으로 secret 키를 변경하고 재배포 해야하는 불편함이 있다고 판단했습니다.<br>
    → Secret key  를 알고리즘을 통해 서버가 재시작되면 자동으로 변경되도록 설정했습니다.<br>
우아한 형제들 코프링 github를 참고했습니다.<br>
[https://github.com/woowacourse/service-apply/blob/master/src/main/kotlin/apply/security/JwtTokenProvider.kt](https://github.com/woowacourse/service-apply/blob/master/src/main/kotlin/apply/security/JwtTokenProvider.kt)

    <img src="https://user-images.githubusercontent.com/103364805/213639070-6fe6d733-4e8c-40f5-b2e9-d43451eb559e.png"  width="800" height="50">

- request 와 response 에 Entity 를 사용하면 해당 Entity에 있는 필드값이 응답되기 때문에 불필요한 정보가 포함될 것이라고 판단했습니다.<br>
→ 필요한 응답값만 가지고 있는 Dto 를 만들어 리팩토링하였습니다.
    <img src="https://user-images.githubusercontent.com/103364805/213639306-dea7793a-1ad5-4aeb-92c3-fa5c2ade8b62.png"  width="750" height="250">
    
- 깃헙액션에서 도커허브 이미지를 pull 하고 컨테이너화 하는 과정에서 i/o timeout 에러가 발생했습니다. 단순히 시간초과 라고 생각하여 time 을 60초(default 30초) 로 늘려주었지만 동일한 에러가 발생했습니다.<br>
→ i/o timeout 관련한 에러는 보통 방화벽 또는 외부에서 접근 불가능한 내부 IP 등으로 인해 서버에 접근하지 못할 때 발생한 에러입니다.
ec2 의 22번 포트 접근범위를 anywhere IPv4 로 바꾸어 주어 해결했습니다.
    <img src="https://user-images.githubusercontent.com/103364805/213639607-f0453880-59f0-41a0-9dc1-2a0207176844.png"  width="650" height="650">
    
# 프로젝트를 진행하면서 학습한 내용과 에러 조치
학습했던 내용과 겪었던 에러 내용을 정리하여, 이후 효율적으로 사용 및 쉽게 이해하기 위해 정리하였습니다.<br>
이후, 현 프로젝트에 적용되어있는 설정방식과 리팩토링을 하면서 해당 프로젝트에 관련된 내용은 지속적으로 최신화 할 예정입니다.

### 학습 내용정리
- [Ehcache 2 -> Ehcache 3 마이그레이션](https://josteady.tistory.com/811)
- [Spring Security + JWT 로그인 구현하기 (Access Token)](https://josteady.tistory.com/838)
- [@ResponseStatus 와 ResponseEntity 차이점](https://josteady.tistory.com/835)
- [JAVA JDK17 을 사용하는 이유](https://josteady.tistory.com/834)
- [Springboot docker GitHub Action 연동하여 자동 배포하기](https://josteady.tistory.com/831)
- [spring boot 빌드 시, 특정 테스트 제외하기](https://josteady.tistory.com/833)
- [RDS timezone Asia/Seoul 로 변경하기](https://josteady.tistory.com/832)
- [AWS Secrets Manager 설정하고 Spring boot 연동하기](https://josteady.tistory.com/830)
- [RDS MYSQL 연결 시 Connection time out 해결하기](https://josteady.tistory.com/829)
- [ec2 linux + docker + spirngboot 프로젝트 + mysql 연동 후 서버 띄우기](https://josteady.tistory.com/828)
- [도커허브 - docker requested access to the resource is denied](https://josteady.tistory.com/827)
- [Docker-compose를 작성해서 SpringBoot + MySql DB 서버 구동](https://josteady.tistory.com/826)
- [@Convert - T타입 + Map 사용하기](https://josteady.tistory.com/771)
- [Centos mysql 비밀번호 재설정 방법](https://josteady.tistory.com/824)
- [ec2 linux mysql8 설치하기](https://josteady.tistory.com/823)
- [Multipart 는 HTTP POST 로만 사용](https://josteady.tistory.com/817)
- [spring boot docker mysql 기본설정](https://josteady.tistory.com/819)
- [@RequestPart MultipartFile , Json 컨트롤러 통합 테스트](https://josteady.tistory.com/814)
- [spring 스케줄러(Scheduler) 적용하기](https://josteady.tistory.com/812)
- [Springboot AOP 적용 + 어노테이션 기반](https://josteady.tistory.com/810)
- [Ehcache 를 사용한 Cache 이용해보기](https://josteady.tistory.com/808)
- [Page 테스트 코드 작성하기](https://josteady.tistory.com/799)
- [swagger 에 jwt token 추가하기](https://josteady.tistory.com/794)
- [H2 데이터베이스 데이터 유지하기](https://josteady.tistory.com/792)
- [JPA 순환 참조 해결해보기](https://josteady.tistory.com/776)
- [data.sql 적용하기 (스프링부트 버전 2.7.x 이상)](https://josteady.tistory.com/759)


### ERROR
- [Failed to load ApplicationContext](https://josteady.tistory.com/836)
- [docker push denied requested access to the resource is denied](https://josteady.tistory.com/822)
- [Only one usage of each socket address (protocol/network address/port) is normally permitted.](https://josteady.tistory.com/818)
- [com.amazonaws.services.s3.model.AmazonS3Exception: The AWS Access Key Id you provided does not exist in our records.](https://josteady.tistory.com/815)
- [object references an unsaved transient instance - save the transient instance before flushing](https://josteady.tistory.com/806)
- [@WebMvcTest 403 응답 처리](https://josteady.tistory.com/802)
- [the input device is not a TTY. If you are using mintty, try prefixing the command with 'winpty'](https://josteady.tistory.com/796)
- [Illegal DefaultValue null for parameter type integer](https://josteady.tistory.com/795)
- [AWS S3 access denied Error](https://josteady.tistory.com/793)
- [query did not return a unique result](https://josteady.tistory.com/791)
- [javax/xml/bind/DatatypeConverter](https://josteady.tistory.com/783)
- ['script' must not be null or empty](https://josteady.tistory.com/775)
- [Referential integrity constraint violation](https://josteady.tistory.com/773)
- [Error creating bean with name 'swaggerConfig' defined in file](https://josteady.tistory.com/768)
- [JPA metamodel must not be empty!](https://josteady.tistory.com/767)
- [attempted to assign id from null one-to-one property](https://josteady.tistory.com/765)
- [No serializer found for class org.hibernate.proxy.pojo.bytebuddy.ByteBuddyInterceptor and no properties discovered to create BeanSerializer](https://josteady.tistory.com/760)
- [No validator could be found for constraint 'javax.validation.constraints.NotBlank' validating type 'java.lang.Integer'](https://josteady.tistory.com/725)

# 프로젝트를 하면서 느낀 점
1. 프로젝트를 하면서 나의 위치와 수준을 되돌아볼 수 있었습니다.
2. 디버깅의 중요성을 깨달았습니다.
3. 구현 전 분석, 이해, 정리가 충분히 되어야 한다고 생각합니다.
4. “코드가 기능이 정상적으로 된다” 라는 것이 끝이 될수도, 출발점이 될 수 있다고 생각합니다.
5. 공부하지 않았던 부분에서 끈질기게 붙잡아 해결하면서 새로운 기술 습득에 대한 흥미를 높일 수 있었습니다.
6. 다른 사람의 목소리를 들을 수록 자신과 프로젝트의 완성도를 높일 수 있다고 생각합니다.
7. 테스트 하기 쉬운 코드가 좋은 코드라고 생각했습니다.
8. 코드를 망가뜨리고 많은 에러가 발생하면서 시행착오를 겪으면서 더 많이 배우고 성장했습니다.
