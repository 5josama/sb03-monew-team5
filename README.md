# 🪖 5조 5죠 사마 - MoNew 프로젝트

🔗 [프로젝트 노션 ](https://www.notion.so/ohgiraffers/5-207649136c1180968b6bf8028b42b212)

---
## 👥 팀원 구성
| **팀원** | 주요 기능 담당 | 프로젝트 담당 |  Git  |
| --- | --- | --- | --- |
| 강호 | 뉴스 기사 관리 | AWS 배포 | https://github.com/kangho1870 |
| 강문구 | 댓글 관리  | PM | https://github.com/Kangmoongu |
| 강우진 | 알림 관리  | 문서(노션,회의록) | https://github.com/bladnoch |
| 김동욱 | 관심사 관리 | CI & CD(Github Actions) |  |
| 박진솔 | 활동 내역 관리, 사용자 관리 | Git관리 |  |

---
## 📌 프로젝트 소개
> 여러 뉴스 API를 통합하여 사용자에게 맞춤형 뉴스를 제공하고, 의견을 나눌 수 있는 소셜 기능을 갖춘 서비스

 - 프로젝트명: MoNew 
 - 진행기간: 2025.07.09 ~ 2025.07.30

---

## 🛠 기술 스택
### Backend
- Framework: Spring Boot 3.5.0
- Data Access: Spring Data JPA, JDBC
- Scheduler: Spring Scheduler
- Build Tool: Gradle (Java 17 Toolchain)
- 문서화: SpringDoc OpenAPI
- QueryDSL: 5.0.0 (Jakarta 기반)
- DTO 매핑: MapStruct 1.5.5.Final
- 기타: Lombok
### Database
- PostgreSQL (운영용)
- H2 (로컬/테스트용)

### 협업 도구
- Git & GitHub
- Discord
- ZEP
  
---

## ✨ 구현 기능
### 👤강호
### 뉴스 기사 수집

- **매 시간마다 Spring Batch**를 통해 뉴스 기사 자동 수집
- 수집 대상:
    - NAVER 뉴스 검색 API
    - RSS 피드: 한국경제, 조선일보, 연합뉴스
- 수집 조건:
    - **RDS에 저장된 키워드가 제목 또는 본문에 포함된 기사만 저장**

### 뉴스 기사 목록 조회

- 검색 조건:
    - `제목` 또는 `요약`의 **부분 일치 검색**
- 필터링:
    - `관심사(Interest)`
    - `출처(Source)`
    - `날짜 범위`
- 정렬 조건 (택1):
    - `날짜`, `댓글 수`, `조회 수`

### 뉴스 기사 백업

- **매일 새벽 2시**, Spring Batch를 이용한 자동 백업 수행
- 백업 전략:
    - **직전 `articleBackupJob` 수행 이후부터의 신규 기사만 증분 백업**
- 저장 포맷:
    - `JSON` 직렬화 후 AWS S3 업로드
    - 예시: `backup/news_2025-07-25.json`

### 뉴스 기사 복구

- 지정한 날짜 범위의 백업 파일을 불러와 **유실된 기사 복구**
- 복구 로직:
    - `sourceUrl`(유일값)을 기준으로 RDS와 비교
    - 존재하지 않는 기사만 신규 저장
- 대량 데이터 대응:
    - **Executor를 이용한 병렬 처리**로 성능 향상
### 강문구

### 🏢 강우진
### 알림 등록
#### 뉴스 기사 알림
- 매시간 실행되는 **기사 수집 배치 작업** 에서 다음과 같은 흐름으로 알림을 생성합니다:
  1. 기사 수집 Step이 완료되면
  2. 관련된 관심사를 구독 중인 사용자들을 대상으로
  3. 관심사에 매칭된 기사 개수에 기반한 알림을 생성합니다.
- **알림 메시지 예시:**
  > `[관심사]와 관련된 기사가 3건 등록되었습니다.`

#### 댓글 좋아요 알림
- 사용자의 댓글에 좋아요가 눌리는 순간, 알림 등록 서비스를 호출하여 해당 댓글 작성자에게 알림을 생성합니다.
- **알림 메시지 예시:**
  > `[이름름]님이 나의 댓글을 좋아합니다.`


### 알림 수정
- **단일 알림 확인**: 알림 하나만 `confirmed = true`로 변경
- **전체 알림 확인**: 해당 사용자의 모든 미확인 알림을 일괄 수정 처리


### 알림 삭제
- 확인한 알림 중 **1주일이 경과된 알림**은 자동으로 삭제됩니다.
- `Spring Scheduler + Batch Job` 조합으로, 매일 배치 작업이 실행됩니다.


### 알림 목록 조회
- **확인하지 않은 알림만** 조회합니다.
- 정렬 기준: `createdAt ASC`
- **QueryDSL + 커서 기반 페이지네이션** 적용으로 성능 최적화된 조회 구현

### 👨‍💻 김동욱

### 관심사 등록 기능
- 관심사 등록 기능 구현
- 관심사는 최대 **10개의 키워드**를 가질 수 있도록 제한
- PostgreSQL의 `pg_trgm` 확장과 `GIN` 인덱스를 활용하여  
  데이터베이스 단에서 기존 관심사 이름과 **80% 이상 유사**할 경우 등록을 차단하는 로직 구현

### 관심사 키워드 수정 기능
- 기존 관심사에 대해 키워드를 **추가/수정**할 수 있는 기능 구현

### 관심사 삭제 기능
- 관심사 삭제 기능 구현
- 관심사 삭제 시, 해당 관심사에 연결된 **키워드도 함께 삭제**되도록 처리

### 관심사 목록 조회 기능
- `GET` 요청을 통해 관심사 목록을 조회하는 API 구현
- 정렬 기준: 관심사 이름, 구독자 수
- **커서 기반 페이지네이션** 방식 적용
  - 커서 기준 값이 동일할 경우, **생성일을 타이브레이커**로 사용해 안정적인 정렬 구현
- `QueryDSL`을 활용하여 검색 조건(관심사 이름, 키워드)에 **부분일치하는 데이터 필터링** 기능 구현

### 관심사 구독 기능
- 사용자가 관심사를 **구독**할 수 있는 기능 구현

### 관심사 구독 취소 기능
- 사용자가 구독 중인 관심사를 **구독 취소**할 수 있는 기능 구현

### CI/CD 파이프라인 구축
- `GitHub Actions`를 활용한 **CI/CD 파이프라인** 구축
- Pull Request 생성 시 자동으로 **테스트가 수행**되도록 설정
- `Codecov`를 연동하여 **테스트 커버리지 80% 이상** 달성을 목표로 커버리지 측정 기능 추가


### 🗂 박진솔

---

## 🌐 배포 주소
AWS를 통해 배포된 웹 페이지

🔗 MoNew 웹페이지

---

## 💬 프로젝트 회고록
발표자료 또는 회고 링크를 여기에 첨부하세요

📄 프로젝트 회고록

--- 
## 파일 구조

---

# sb03-monew-team5
modu's news

[![codecov](https://codecov.io/gh/5josama/sb03-monew-team5/graphs/tree.svg?token=2A4E6S6XJ9)](https://codecov.io/github/5josama/sb03-monew-team5)

[![codecov](https://codecov.io/github/5josama/sb03-monew-team5/graphs/sunburst.svg?token=2A4E6S6XJ9)](https://codecov.io/github/5josama/sb03-monew-team5)

[![codecov](https://codecov.io/github/5josama/sb03-monew-team5/graphs/icicle.svg?token=2A4E6S6XJ9)](https://codecov.io/github/5josama/sb03-monew-team5)

[![codecov](https://codecov.io/github/5josama/sb03-monew-team5/graph/badge.svg?token=2A4E6S6XJ9)](https://codecov.io/github/5josama/sb03-monew-team5)

## prod 환경 실행
docker compose up --build

### .env 파일 변수

SPRING_PROFILES_ACTIVE=prod

POSTGRES_USER=monew_user

POSTGRES_PASSWORD=monew1234

POSTGRES_DB=monew

SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/monew

MONGO_USER=monew_user

MONGO_PASSWORD=monew1234

X_Naver_Client_Id

X_Naver_Client_Secret

AWS_S3_ACCESS_KEY

AWS_S3_SECRET_KEY

AWS_S3_REGION

AWS_S3_BUCKET



