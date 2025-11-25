# Prometheus + Grafana 모니터링 가이드

> 실시간 시스템 메트릭 수집 및 시각화 대시보드

**작성일**: 2025.11.25

---

## 📋 목차
1. [개요](#개요)
2. [접속 정보](#접속-정보)
3. [Grafana 대시보드 설정](#grafana-대시보드-설정)
4. [커스텀 메트릭 확인](#커스텀-메트릭-확인)
5. [알람 설정](#알람-설정)
6. [문제 해결](#문제-해결)

---

## 개요

### 설치된 구성요소
- **Prometheus** (9090 포트): 메트릭 수집 및 저장
- **Grafana** (3001 포트): 메트릭 시각화 대시보드
- **Spring Boot Actuator**: 애플리케이션 메트릭 노출

### 수집되는 메트릭
```
✅ 시스템 메트릭 (CPU 사용률)
✅ JVM 메트릭 (Heap Memory, GC, Thread)
✅ HTTP 요청 (TPS, 응답 시간, 에러율)
✅ 데이터베이스 (Connection Pool 사용률)
```

---

## 접속 정보

### Prometheus
```
URL: http://localhost:9090
설명: 메트릭 쿼리 및 상태 확인
```

### Grafana
```
URL: http://localhost:3001
Username: admin
Password: admin1234
```

### Spring Boot Actuator
```
Health Check: http://localhost:8080/actuator/health
Prometheus Metrics: http://localhost:8080/actuator/prometheus
모든 엔드포인트: http://localhost:8080/actuator
```

---

## Grafana 대시보드 설정

### 1단계: Grafana 로그인

1. 브라우저에서 http://localhost:3001 접속
2. Username: `admin`, Password: `admin1234` 입력
3. 로그인

### 2단계: Data Source 추가

1. 좌측 메뉴 > **Connections** > **Data Sources** 클릭
2. **Add data source** 클릭
3. **Prometheus** 선택
4. 설정 입력:
   ```
   Name: Prometheus
   URL: http://prometheus:9090
   ```
5. 하단 **Save & Test** 클릭
6. ✅ "Successfully queried the Prometheus API" 확인

### 3단계: 기본 대시보드 Import

#### Option 1: Spring Boot 공식 대시보드
1. 좌측 메뉴 > **Dashboards** 클릭
2. 우측 상단 **New** > **Import** 클릭
3. **Import via grafana.com** 입력란에 `4701` 입력 (Spring Boot 2.1+ Dashboard)
4. **Load** 클릭
5. Data Source에서 **Prometheus** 선택
6. **Import** 클릭

#### Option 2: JVM 대시보드
1. Import 화면에서 `12900` 입력 (JVM Micrometer)
2. Load → Prometheus 선택 → Import

#### Option 3: 커스텀 대시보드 (직접 생성)
1. **New** > **New dashboard** 클릭
2. **Add visualization** 클릭
3. 아래 쿼리 예시 참고하여 패널 생성

---

## 주요 메트릭 쿼리

#### CPU 사용률
```promql
system_cpu_usage * 100
```

#### Heap Memory 사용률
```promql
(jvm_memory_used_bytes{area="heap"} / jvm_memory_max_bytes{area="heap"}) * 100
```

#### HTTP 요청 수 (초당)
```promql
rate(http_server_requests_seconds_count[1m])
```

#### HTTP 평균 응답 시간
```promql
rate(http_server_requests_seconds_sum[1m]) /
rate(http_server_requests_seconds_count[1m])
```

#### HTTP P95 응답 시간
```promql
histogram_quantile(0.95,
  rate(http_server_requests_seconds_bucket[1m])
)
```

#### GC 시간
```promql
rate(jvm_gc_pause_seconds_sum[1m])
```

#### DB Connection Pool 사용률
```promql
(hikaricp_connections_active / hikaricp_connections_max) * 100
```

---

## 알람 설정

### 1. Slack으로 알람 보내기

#### Grafana Contact Point 설정
1. 좌측 메뉴 > **Alerting** > **Contact points** 클릭
2. **New contact point** 클릭
3. 설정:
   ```
   Name: Slack Alerts
   Integration: Slack
   Webhook URL: <당신의 Slack Webhook URL>
   ```
4. **Test** 클릭하여 테스트 메시지 확인
5. **Save contact point** 클릭

#### 알람 규칙 생성 예시

**알람 1: CPU 사용률 80% 초과**
```
1. Alerting > Alert rules > New alert rule
2. Query:
   A: system_cpu_usage * 100
3. Condition:
   WHEN avg() OF A IS ABOVE 80
4. Evaluation:
   Every: 1m (1분마다 체크)
   For: 5m (5분 동안 지속 시 알람)
5. Contact point: Slack Alerts
6. Summary: ⚠️ CPU 사용률 80% 초과!
```

**알람 2: API 응답 시간 1초 초과**
```
Query:
histogram_quantile(0.95,
  rate(http_server_requests_seconds_bucket[1m])
)

Condition: WHEN avg() OF A IS ABOVE 1
Summary: 🐌 API P95 응답 시간 1초 초과!
```

**알람 3: Heap Memory 90% 초과**
```
Query:
(jvm_memory_used_bytes{area="heap"} / jvm_memory_max_bytes{area="heap"}) * 100

Condition: WHEN avg() OF A IS ABOVE 90
Summary: 💥 Heap Memory 90% 초과! OOM 위험!
```

---

## 대시보드 구성 예시

### 대시보드 1: 시스템 Overview

```
┌────────────────────────────────────────────────┐
│  📊 System Health Dashboard                    │
├────────────────────────────────────────────────┤
│  CPU Usage: 45%     Memory: 60%     GC: 20ms  │
├───────────────┬────────────────────────────────┤
│  HTTP Requests (TPS)                           │
│  ╱╲                                            │
│ ╱  ╲      ╱╲                                   │
│╱    ╲    ╱  ╲                                  │
│      ╲__╱    ╲___                              │
├───────────────┬────────────────────────────────┤
│  API Response Time (P95)                       │
│  200ms                                         │
├────────────────────────────────────────────────┤
│  Active DB Connections: 25 / 50                │
└────────────────────────────────────────────────┘
```

### 대시보드 2: Slack 알림 모니터링

```
┌────────────────────────────────────────────────┐
│  📢 Slack Notification Dashboard               │
├────────────────────────────────────────────────┤
│  성공: 1,245        실패: 5      성공률: 99.6% │
├────────────────────────────────────────────────┤
│  Slack 알림 성공/실패 추이                      │
│  성공 ─────────────────                        │
│  실패 ╱╲                                       │
│     ╱  ╲                                       │
└────────────────────────────────────────────────┘
```

---

## 문제 해결

### 1. Prometheus가 Spring Boot에서 메트릭을 수집하지 못함

**증상:**
- Prometheus Target이 DOWN 상태
- `http://localhost:9090/targets`에서 빨간색 표시

**해결 방법:**

#### A. Spring Boot 서버 실행 확인
```bash
curl http://localhost:8080/actuator/prometheus
```
→ 200 OK와 메트릭 데이터가 나와야 함

#### B. Docker 네트워크 확인
```bash
# Docker 컨테이너에서 호스트 접근 테스트
docker exec shoppingmall-prometheus wget -O- http://host.docker.internal:8080/actuator/health
```

#### C. prometheus.yml 확인
```yaml
scrape_configs:
  - job_name: 'spring-boot-app'
    static_configs:
      - targets: ['host.docker.internal:8080']  # 이 부분 확인
```

---

### 2. Grafana에서 데이터가 안 보임

**증상:**
- 대시보드는 만들어졌으나 그래프에 "No data" 표시

**해결 방법:**

#### A. Data Source 연결 확인
```
Grafana > Configuration > Data Sources > Prometheus
Test 버튼 클릭 → ✅ 성공 확인
```

#### B. 시간 범위 확인
```
대시보드 우측 상단에서 시간 범위 변경
Last 5 minutes → Last 1 hour
```

#### C. 쿼리 확인
```
Prometheus UI에서 직접 쿼리 테스트:
http://localhost:9090

쿼리 입력: system_cpu_usage
Execute → 그래프에 데이터 나오는지 확인
```

---

### 3. 커스텀 메트릭이 안 보임

**증상:**
- `slack_notification_success_total` 쿼리 시 "No data"

**해결 방법:**

#### A. 메트릭 생성 확인
```bash
# Actuator 엔드포인트에서 메트릭 확인
curl http://localhost:8080/actuator/prometheus | grep slack

출력 예시:
# HELP slack_notification_success_total Slack 알림 전송 성공 횟수
# TYPE slack_notification_success_total counter
slack_notification_success_total{service="notification",} 5.0
```

→ 출력 없으면: 주문을 한 번도 생성하지 않아서 메트릭이 아직 생성되지 않음
→ **해결**: 주문 1개 생성 후 다시 확인

#### B. Prometheus가 수집했는지 확인
```
Prometheus UI: http://localhost:9090
쿼리: slack_notification_success_total
Execute → 값이 나와야 함
```

---

### 4. Docker 컨테이너가 안 뜨거나 재시작 반복

**증상:**
```bash
docker ps
# prometheus나 grafana가 안 보임
```

**해결 방법:**

#### A. 로그 확인
```bash
docker logs shoppingmall-prometheus
docker logs shoppingmall-grafana
```

#### B. prometheus.yml 문법 오류
```bash
# Prometheus 컨테이너 재시작
docker restart shoppingmall-prometheus

# 로그 확인
docker logs -f shoppingmall-prometheus
```

#### C. 포트 충돌
```bash
# 9090 포트 사용 중인지 확인
lsof -i :9090
lsof -i :3001

# 사용 중이면 docker-compose.yml에서 포트 변경
ports:
  - "9091:9090"  # 9090 → 9091로 변경
```

---

## 유용한 명령어

### Docker 관리
```bash
# 컨테이너 시작
docker-compose up -d prometheus grafana

# 컨테이너 중지
docker-compose stop prometheus grafana

# 컨테이너 재시작
docker restart shoppingmall-prometheus shoppingmall-grafana

# 로그 확인
docker logs -f shoppingmall-prometheus
docker logs -f shoppingmall-grafana

# 컨테이너 삭제 (데이터 유지)
docker-compose down

# 컨테이너 + 볼륨 삭제 (데이터도 삭제)
docker-compose down -v
```

### 메트릭 확인
```bash
# Actuator Health Check
curl http://localhost:8080/actuator/health

# Prometheus 메트릭 확인
curl http://localhost:8080/actuator/prometheus

# 특정 메트릭만 확인
curl http://localhost:8080/actuator/prometheus | grep cpu

# Slack 메트릭 확인
curl http://localhost:8080/actuator/prometheus | grep slack_notification
```

---

## 참고 자료

### 공식 문서
- [Prometheus Documentation](https://prometheus.io/docs/)
- [Grafana Documentation](https://grafana.com/docs/)
- [Spring Boot Actuator](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html)
- [Micrometer](https://micrometer.io/docs)

### Grafana Dashboard ID
- **4701**: Spring Boot 2.1+ Statistics
- **12900**: JVM (Micrometer)
- **11378**: JMX Overview
- **6756**: Spring Boot Statistics

### PromQL 학습
- [PromQL Basics](https://prometheus.io/docs/prometheus/latest/querying/basics/)
- [PromQL Examples](https://prometheus.io/docs/prometheus/latest/querying/examples/)

---

## 다음 단계

1. ✅ 기본 대시보드 생성
2. ✅ Slack 알림 메트릭 확인
3. 📋 알람 규칙 설정 (CPU, Memory, Slack 실패율)
4. 📋 주문 관련 메트릭 추가 (주문 수, 총 금액 등)
5. 📋 RabbitMQ 메트릭 추가

---

**작성자**: Claude Code
**버전**: 1.0.0
**최종 수정일**: 2025.11.25
