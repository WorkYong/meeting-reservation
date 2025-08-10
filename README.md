# 🗓️ 사내 회의실 예약 & 결제 데모 서비스

## 1. 프로젝트 개요 & 기술 스택/버전

### 프로젝트 개요

- WiseAi 사내 회의실 예약 & 결제 데모 서비스
- 예약 생성/수정/취소, 결제 요청, 결제 웹훅 수신 및 상태 전이
- 모든 API 응답은 공통 포맷 `ApiResult` 사용

### 기술 스택

- **Backend**: Spring Boot (Java 21+), Spring Web, Validation, Lombok
- **API 문서**: springdoc-openapi (Swagger UI)
- **DB**: MySQL 8.0 (Docker)
- **Mock 결제 서버**: WireMock 3.x (Docker)
- **Infra**: Docker, Docker Compose

### 포트 & 엔드포인트

- **App**: `http://localhost:8080`
- **DB(MySQL)**: `localhost:3306`
- **MockPay(WireMock)**: `http://localhost:8081`

---

## 2. docker-compose up 실행 방법

### 0) 사전 준비

- git clone https://github.com/WorkYong/meeting-reservation.git
  cd meeting-reservation

- Docker, Docker Compose 설치
- 프로젝트 루트 구조:
  ```
  /project-root
  ├─ docker-compose.yml
  ├─ db/
  │  └─ init/
  │     └─ 01-init.sql        # DB 스키마 + 시드
  └─ mockpay/
     ├─ mappings/             # WireMock 매핑 (선택)
     └─ __files/              # WireMock 파일 (선택)
  ```

### 1) `.env` 환경 변수 설정

> ⚠️ 현재는 GitHub에 업로드된 상태이지만, 실제 운영 환경에서는 **`.gitignore`**에 추가하여 비공개로 관리해야 합니다.

```env
MYSQL_USER=app
MYSQL_PASSWORD=app1234
MYSQL_ROOT_PASSWORD=changeme
```

### 2) 첫 실행(완전 초기화)

```bash
docker compose down -v          # 볼륨 포함 정리 (최초/스키마 변경 시 권장)
docker compose up -d
docker compose logs -f db       # init.sql 실행 로그 확인
```

### 3) 일반 실행 / 코드만 수정했을 때

```bash
docker compose up -d --build    # 앱만 새로 빌드해서 실행
```

### 4) 헬스체크 대기

```bash
docker compose ps
```

`db`, `mockpay`가 healthy 상태가 되면 `app`이 자동 기동됩니다.

### 5) DB 시드 확인 (옵션)

```bash
docker exec -it meeting-reservation-db   mysql -uroot -p"$MYSQL_ROOT_PASSWORD"   -e "USE wiseaimeetingreservation; SELECT COUNT(*) FROM users; SELECT COUNT(*) FROM reservations;"
```

---

## 3. Swagger UI 접속 방법

- **Swagger UI**:  
  <http://localhost:8080/docs>
- **OpenAPI JSON**:  
  <http://localhost:8080/v3/api-docs>

> 예약/결제/Webhooks API의 요청/응답 예시는 Swagger 문서에 기재되어 있습니다.

---

## 4. 테스트 실행 방법

### 1) 단위/통합 테스트 (로컬)

```bash
./gradlew clean test
```

### 2) 간단 API 수동 테스트 (cURL 예시)

#### 예약 생성

```bash
curl -X POST http://localhost:8080/reservations   -H "Content-Type: application/json"   -d '{
    "userId": 1,
    "meetingRoomId": 1,
    "startTime": "2025-08-15T09:00:00",
    "endTime": "2025-08-15T10:30:00"
  }'
```

#### 결제 요청 (카드)

```bash
curl -X POST http://localhost:8080/payments/3/pay   -H "Content-Type: application/json"   -d '{
    "providerType": "CARD",
    "cardToken": "tok_test_123"
  }'
```

#### 결제 웹훅 (카드 승인)

```bash
curl -X POST http://localhost:8080/webhooks/payments/card   -H "Content-Type: application/json"   -d '{ "txid": "CARD-A1001", "result": "APPROVED", "reason": "ok" }'
```

---

## 3) Swagger UI 접속 & 테스트 방법

1. **접속**

   - 브라우저에서 [http://localhost:8080/docs](http://localhost:8080/docs) 접속
   - API 명세, 요청 파라미터, 응답 형식을 UI로 확인 가능

2. **테스트 실행**

   - 호출할 API 선택 → `Try it out` 클릭
   - 필요한 파라미터 입력 후 `Execute` 버튼 클릭
   - 하단 **Curl / Request URL / Response** 영역에서 요청·응답 확인 가능

3. **예시**
   - 예약 생성 API 테스트:
     - `POST /reservations` 선택
     - `Try it out` → 요청 JSON 입력:
       ```json
       {
         "userId": 1,
         "meetingRoomId": 1,
         "startTime": "2025-08-15T09:00:00",
         "endTime": "2025-08-15T10:30:00"
       }
       ```
     - `Execute` → 200 OK 응답 확인

## 5. 트러블슈팅 체크리스트

- **init.sql 실행 안 됨**
  - `docker compose down -v`로 볼륨 삭제 후 재기동
  - `db/init/01-init.sql` 경로 확인
  - `docker compose logs -f db`에서 init 실행 로그 확인
- **DB 연결 실패**
  - `.env` 사용자/비밀번호 확인
  - `SPRING_DATASOURCE_URL`은 compose에서 `db` 호스트로 설정됨
- **MockPay 호출 실패**
  - 내부 통신은 `localhost` 대신 서비스명 사용 (`mockpay`)
  - `MOCKPAY_BASE_URL=http://mockpay:8081`

---

## 6. 클라우드 아키텍처 & 배포 설계 (Naver Cloud 기반 · 합리적 최저)

> 본 설계는 사내 환경과 비용 절감을 고려하여 **Naver Cloud**를 기반으로 작성되었음.

### 6.1 전제

- 초기 트래픽: 동시 접속 30~50명, 일 최대 3,000 요청
- 결제 처리 안정성 최우선, 무중단 배포 필요
- 1년 내 최대 2~3배 트래픽 증가 가능성 고려
- 국내 사용자 100%, 금융/결제 보안 규제(ISMS-P, KISA) 준수 필요
- 사내 운영 인원 80명 규모, 전담 DevOps 인력 제한적

### 6.2 아키텍처 개요

```
사용자 ──(HTTPS)──> Naver Cloud Load Balancer ──> NKS (네임스페이스: prod)
                         ├─ api-deploy (Spring Boot)   → HPA 자동확장
                         ├─ webhook-deploy (Webhook)   → HPA 자동확장
                         └─ jobs/cron (정산/청소)

NKS ↔ Cloud DB for MySQL     # 예약/결제 데이터
NKS ↔ Cloud DB for Redis     # 세션/락/캐시
NKS → Cloud Monitoring       # 로그/메트릭
```

### 6.3 컴포넌트 선택 & 역할

| 계층          | 서비스                        | 선택                        | 역할 |
| ------------- | ----------------------------- | --------------------------- | ---- |
| L7 LB         | **Naver Cloud Load Balancer** | HTTPS 종단, 라우팅          |
| Compute       | **NKS (Kubernetes Service)**  | 컨테이너 배포/확장          |
| DB            | **Cloud DB for MySQL**        | 예약/결제 데이터, 자동 백업 |
| Cache         | **Cloud DB for Redis**        | 세션/락/임시 상태           |
| CI/CD         | **GitHub Actions**            | build→push→deploy           |
| Observability | **Cloud Monitoring**          | 로그/메트릭 수집            |
| Secrets       | **Cloud Secret Manager**      | DB 비밀번호/API 키 관리     |

### 6.4 산출물 구조(예시)

```
infra/
  terraform/              # VPC, Subnet, LB, NKS, DB 스텁
    main.tf
    variables.tf
    outputs.tf
deploy/
  k8s/
    base/
      deployment.yaml
      service.yaml
      ingress.yaml
      hpa.yaml
      configmap.yaml
      secret.yaml
    overlays/prod/
      kustomization.yaml
      values-patch.yaml
.github/
  workflows/
    ci-cd.yml             # build → push → deploy
```

### 6.5 CI/CD 개요

- **CI**: Gradle 빌드 → Docker 이미지 → Naver Cloud Container Registry push
- **CD**: `kubectl apply -k deploy/k8s/overlays/prod`
- **배포 전략**: 롤링 업데이트, 실패 시 자동 롤백

### 6.6 보안·컴플라이언스 요약

- 전 구간 HTTPS
- 최소 권한 IAM 역할 부여
- **Secret Manager**로 민감정보 관리 (DB 비밀번호, 결제 API 키)
- RDS 암호화로 저장 데이터 보호
- **PCI DSS 준수**
  - 카드번호·CVV 등 민감정보는 저장 금지
  - HTTPS/TLS 전송 필수
  - 결제 데이터는 **토큰 기반**으로 처리

### 6.7 비용 비교 예시 (월간, 초기 규모 기준)

| 항목                             | Naver Cloud (월) | AWS (월, USD)          | 비고                      |
| -------------------------------- | ---------------- | ---------------------- | ------------------------- |
| Compute<br>(2 vCPU / 4 GB × 2대) | 60,000 KRW       | 100 USD (~135,000 KRW) | Naver Cloud가 약 40% 저렴 |
| DB – MySQL<br>(2 vCPU / 8 GB)    | 40,000 KRW       | 80 USD (~110,000 KRW)  | Naver Cloud가 약 50% 저렴 |
| Cache – Redis<br>(1 vCPU / 4 GB) | 20,000 KRW       | 40 USD (~52,000 KRW)   | 유사 비용 구조            |
| 네트워크 egress (10 GB 기준)     | 2,000 KRW        | 9 USD (~11,000 KRW)    | 국내망 중심이면 훨씬 저렴 |
| **총합 (예상)**                  | 122,000 KRW      | 160 USD (~200,000 KRW) | 약 **40–50% 비용 절감**   |

> 1 USD ≈ 1,350 KRW 기준  
> 실제 비용은 트래픽, 스토리지, 리전, 할인 옵션(약정/예약 인스턴스) 등에 따라 변동 가능
