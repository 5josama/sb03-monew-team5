# 🕺🏻5죠 사마 - MoNew 프로젝트

🔗 [프로젝트 노션 ](https://www.notion.so/ohgiraffers/5-207649136c1180968b6bf8028b42b212)

---
## 👥 팀원 구성
| **팀원** | 주요 기능 담당 | 프로젝트 담당 |  Git  |
| --- | --- | --- | --- |
| 강호 | 뉴스 기사 관리 | AWS 배포 | https://github.com/kangho1870 |
| 강문구 | 댓글 관리  | PM | https://github.com/Kangmoongu |
| 강우진 | 알림 관리  | 문서(노션,회의록) | https://github.com/WJKANGsw |
| 김동욱 | 관심사 관리 | CI & CD(Github Actions) | https://github.com/bladnoch |
| 박진솔 | 활동 내역 관리, 사용자 관리 | Git관리 | https://github.com/JinsolPark |

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
- Notion
- Discord
- ZEP
  
---

## ✨ 구현 기능
### 👤강호
![강호](https://github.com/user-attachments/assets/6e876023-5ac9-4134-8f6e-02bc11157791)

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

### 뉴스 기사 삭제
#### 논리 삭제 (Soft Delete)
- 뉴스 기사의 데이터를 보존하면서 삭제 처리
- `is_deleted = true`로 업데이트

#### 물리 삭제 (Hard Delete)
- 뉴스 기사 및 관련 데이터 완전 삭제
  - 조회수(ArticleView)
  - 댓글(Comment)

### 모니터링 - Spring Actuator 기반 Custom Metric
- `Prometheus` + `Grafana`를 활용한 실시간 지표 수집 및 시각화
- 정의된 Custom Metric 항목:
  - 전체 뉴스 기사 수 (`article.count`)
  - 전체 유저 수 (`user.count`)
  - 전체 관심사 수 (`interest.count`)
  - 배치 상태 관련 지표:
    - 마지막 배치 성공 여부 (`batch.last.success`) → `0` (실패) / `1` (성공)
    - 누적 배치 성공 횟수 (`batch.success.count`)
    - 누적 배치 실패 횟수 (`batch.fail.count`)

### 로그 관리 및 S3 업로드

- 요청별 로그 식별을 위한 MDC 설정:
  - 요청 ID
  - 클라이언트 IP
- 응답 헤더에 로그 추적 ID 포함
- 로그 파일은 **날짜 기준**으로 분리되어 AWS S3에 업로드됨
  - 예시: `logs/application.2025-07-25.log`

---
### 🤣 강문구
![강문구](https://github.com/user-attachments/assets/a9ee3007-9bf5-4574-9509-09e26b4510d4)

### MoNew 댓글 시스템 기능 명세
### 댓글 정보 구조
댓글은 다음의 정보를 포함합니다:
- 뉴스 기사 정보
- 사용자 정보
- 내용
- 날짜
- 좋아요

### 댓글 등록
- 뉴스 기사 별 댓글을 등록할 수 있습니다.

### 댓글 수정
- 본인이 작성한 댓글만 수정할 수 있습니다.

### 댓글 삭제
- **논리 삭제**를 기본 원칙으로 하여 관련된 정보가 유지되도록 합니다.
- **물리 삭제** 시에는 관련된 정보도 모두 삭제되도록 구현했습니다.

> ⚠️ **주의사항**  
> 물리 삭제 기능은 UI로 제공하지 않으며, 테스트 코드를 통해 검증합니다.

### 뉴스 기사 별 댓글 목록 조회
다음의 속성으로 정렬 및 커서 페이지네이션을 구현했습니다:
- **정렬 기준**: 날짜, 좋아요 수
- **제약 조건**: 선택적으로 1개의 정렬 조건만 가질 수 있습니다.
- **페이지네이션**: 커서 기반 페이지네이션으로 성능 최적화

### 댓글 좋아요 기능
- 댓글마다 좋아요 또는 좋아요 취소할 수 있습니다.

### 기술적 특징
- **QueryDSL + 커서 기반 페이지네이션** 적용으로 대용량 데이터 조회 성능 최적화
- 논리/물리 삭제 정책을 통한 데이터 무결성 보장
- 사용자 권한 기반 수정 제한으로 보안성 확보

---
### 🏢 강우진
![강우진](https://github.com/user-attachments/assets/87442421-b559-4817-bbb5-f1e43d9300b1)

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
  > `[이름]님이 나의 댓글을 좋아합니다.`


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

---
### 👨‍💻 김동욱
![김동욱](https://github.com/user-attachments/assets/c22ca572-526f-4757-8d2c-cb38942fb9f0)

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

---
### 🗂 박진솔
![ezgif com-crop](https://github.com/user-attachments/assets/9a3719b9-9f05-4761-9b3a-53b5c54441f5)

### 사용자 관리
- `이메일`, `닉네임`, `비밀번호`로 **등록**
- `닉네임` **수정**
- **논리 삭제, 물리 삭제**
  - 물리 삭제 시 관련 도메인 소스 삭제 후 사용자 삭제
  - 삭제되는 도메인 소스
    - `구독한 관심사`
    - `작성한 댓글`
    - `좋아요한 댓글`
    - `조회한 뉴스 기사`
    - `알림` 목록
- `이메일`, `비밀번호`로 **로그인**

### 활동 내역 관리
- 사용자 별 활동 내역 **조회**
    - `사용자 정보`
    - `구독 중인 관심사`
    - 최근 `작성한 댓글` 최대 10건
    - 최근 `좋아요를 누른 댓글` 최대 10건
    - 최근 `본 뉴스 기사` 최대 10건

---

## 🌐 배포 주소
AWS를 통해 배포된 웹 페이지

[🔗 MoNew 웹페이지](http://13.125.219.34)

---

## 💬 프로젝트 회고록
발표자료 또는 회고 링크를 여기에 첨부하세요

📄 프로젝트 회고록

--- 
## 파일 구조
```
\---com
    \---sprint5team
        \---monew
            |   MonewApplication.java
            |   
            +---base
            |   +---aop
            |   |       MetricAspect.java
            |   |       
            |   +---config
            |   |       AppConfig.java
            |   |       ArticleBackupJobConfig.java
            |   |       ArticleScraperJobConfig.java
            |   |       BatchMetaInitializerConfig.java
            |   |       JpaAuditingConfig.java
            |   |       MDCLoggingInterceptor.java
            |   |       QuerydslConfig.java
            |   |       SchedulerConfig.java
            |   |       WebMvcConfig.java
            |   |       
            |   +---entity
            |   |       BaseEntity.java
            |   |       BaseUpdatableEntity.java
            |   |       
            |   +---exception
            |   |       BaseException.java
            |   |       ErrorResponse.java
            |   |       GlobalExceptionHandler.java
            |   |       
            |   +---health
            |   |       MonewHealthIndicator.java
            |   |       
            |   +---metric
            |   |       MonewMetrics.java
            |   |       
            |   +---service
            |   |       BatchMetadataService.java
            |   |       BatchStatusService.java
            |   |       
            |   \---util
            |           ArticleJsonBatchWriter.java
            |           ArticleScraperTasklet.java
            |           InterestMatcher.java
            |           NotificationTasklet.java
            |           S3Storage.java
            |           
            \---domain
                +---article
                |   +---controller
                |   |       ArticleApi.java
                |   |       ArticleController.java
                |   |       
                |   +---dto
                |   |       ArticleCommentCount.java
                |   |       ArticleDto.java
                |   |       ArticleRestoreResultDto.java
                |   |       ArticleViewDto.java
                |   |       CursorPageFilter.java
                |   |       CursorPageResponseArticleDto.java
                |   |       
                |   +---entity
                |   |       Article.java
                |   |       ArticleCount.java
                |   |       ArticleKeyword.java
                |   |       
                |   +---exception
                |   |       ArticleNotFoundException.java
                |   |       
                |   +---mapper
                |   |       ArticleMapper.java
                |   |       ArticleViewMapper.java
                |   |       
                |   +---repository
                |   |       ArticleCountCustomRepository.java
                |   |       ArticleCountCustomRepositoryImpl.java
                |   |       ArticleCountRepository.java
                |   |       ArticleCustomRepository.java
                |   |       ArticleCustomRepositoryImpl.java
                |   |       ArticleRepository.java
                |   |       
                |   +---service
                |   |       ArticleScraper.java
                |   |       ArticleService.java
                |   |       ArticleServiceImpl.java
                |   |       
                |   \---util
                |           ArticleBackUpScheduler.java
                |           ArticleConsumer.java
                |           ArticleQueueManager.java
                |           ArticleScraperScheduler.java
                |           KeywordConsumer.java
                |           KeywordQueueManager.java
                |           NaverNewsApiClient.java
                |           
                +---comment
                |   +---controller
                |   |       CommentApi.java
                |   |       CommentController.java
                |   |       
                |   +---dto
                |   |       CommentActivityDto.java
                |   |       CommentDto.java
                |   |       CommentLikeActivityDto.java
                |   |       CommentLikeDto.java
                |   |       CommentRegisterRequest.java
                |   |       CommentUpdateRequest.java
                |   |       CursorPageResponseCommentDto.java
                |   |       
                |   +---entity
                |   |       Comment.java
                |   |       Like.java
                |   |       
                |   +---exception
                |   |       AlreadyLikedException.java
                |   |       CommentException.java
                |   |       CommentNotFoundException.java
                |   |       LikeNotFoundException.java
                |   |       
                |   +---mapper
                |   |       CommentMapper.java
                |   |       LikeMapper.java
                |   |       
                |   +---repository
                |   |       CommentRepository.java
                |   |       CommentRepositoryCustom.java
                |   |       CommentRepositoryImpl.java
                |   |       LikeRepository.java
                |   |       
                |   +---service
                |   |       CommentService.java
                |   |       CommentServiceImpl.java
                |   |       
                |   \---util
                |           CommentLikedEvent.java
                |           CommentLikedEventListener.java
                |           
                +---interest
                |   +---controller
                |   |       InterestApi.java
                |   |       InterestController.java
                |   |       
                |   +---dto
                |   |       CursorPageRequest.java
                |   |       CursorPageResponseInterestDto.java
                |   |       InterestDto.java
                |   |       InterestRegisterRequest.java
                |   |       
                |   +---entity
                |   |       .DS_Store
                |   |       Interest.java
                |   |       
                |   +---exception
                |   |       InterestNotExistsException.java
                |   |       SimilarInterestException.java
                |   |       
                |   +---mapper
                |   |       InterestMapper.java
                |   |       
                |   +---repository
                |   |       InterestRepository.java
                |   |       InterestRepositoryCustom.java
                |   |       InterestRepositoryImpl.java
                |   |       
                |   \---service
                |           InterestService.java
                |           InterestServiceImpl.java
                |           
                +---keyword
                |   +---dto
                |   |       InterestUpdateRequest.java
                |   |       
                |   +---entity
                |   |       Keyword.java
                |   |       
                |   +---exception
                |   |       NoKeywordsToUpdateException.java
                |   |       
                |   \---repository
                |           KeywordRepository.java
                |           
                +---notification
                |   +---batch
                |   |       NotificationDeleteBatchConfig.java
                |   |       NotificationDeleteScheduler.java
                |   |       NotificationDeleteTasklet.java
                |   |       
                |   +---controller
                |   |       NotificationApi.java
                |   |       NotificationController.java
                |   |       
                |   +---dto
                |   |       CursorPageResponseNotificationDto.java
                |   |       NotificationDto.java
                |   |       
                |   +---entity
                |   |       Notification.java
                |   |       ResourceType.java
                |   |       
                |   +---exception
                |   |       InvalidRequestParameterException.java
                |   |       NotificationNotFoundException.java
                |   |       
                |   +---mapper
                |   |       NotificationMapper.java
                |   |       
                |   +---repository
                |   |       NotificationRepository.java
                |   |       NotificationRepositoryCustom.java
                |   |       NotificationRepositoryImpl.java
                |   |       
                |   \---service
                |           NotificationService.java
                |           NotificationServiceImpl.java
                |           
                +---user
                |   +---controller
                |   |       UserActivityApi.java
                |   |       UserActivityController.java
                |   |       UserApi.java
                |   |       UserController.java
                |   |       
                |   +---dto
                |   |       UserActivityDto.java
                |   |       UserDto.java
                |   |       UserLoginRequest.java
                |   |       UserRegisterRequest.java
                |   |       UserUpdateRequest.java
                |   |       
                |   +---entity
                |   |       User.java
                |   |       
                |   +---exception
                |   |       InvalidInputValueException.java
                |   |       InvalidLoginException.java
                |   |       UserAlreadyExistsException.java
                |   |       UserNotFoundException.java
                |   |       
                |   +---mapper
                |   |       UserMapper.java
                |   |       
                |   +---repository
                |   |       UserRepository.java
                |   |       
                |   \---service
                |           UserActivityService.java
                |           UserActivityServiceImpl.java
                |           UserService.java
                |           UserServiceImpl.java
                |           
                \---user_interest
                    +---controller
                    |       UserInterestApi.java
                    |       UserInterestController.java
                    |       
                    +---dto
                    |       SubscriptionDto.java
                    |       
                    +---entity
                    |       UserInterest.java
                    |       
                    +---exception
                    |       InvalidSubscriptionRequestException.java
                    |       SubscriberNotMatchesException.java
                    |       
                    +---mapper
                    |       UserInterestMapper.java
                    |       
                    +---repository
                    |       UserInterestRepository.java
                    |       
                    \---service
                            UserInterestService.java
                            UserInterestServiceImpl.java
                            

```
---

# sb03-monew-team5
modu's news

[![codecov](https://codecov.io/gh/5josama/sb03-monew-team5/graphs/tree.svg?token=2A4E6S6XJ9)](https://codecov.io/github/5josama/sb03-monew-team5)

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



