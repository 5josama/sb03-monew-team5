# 🪖 5조 5죠 사마 - MoNew 프로젝트

🔗 [프로젝트 노션 ](https://www.notion.so/ohgiraffers/5-207649136c1180968b6bf8028b42b212)

---
## 👥 팀원 구성
| **팀원** | 주요 기능 담당 | 프로젝트 담당 |  Git  |
| --- | --- | --- | --- |
| 강호 | 뉴스 기사 관리 | AWS 배포 | https://github.com/kangho1870 |
| 강문구 | 댓글 관리  | PM | https://github.com/Kangmoongu |
| 강우진 | 알림 관리  | 문서(노션,회의록) |  |
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

### 👨‍💻 김동욱

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



