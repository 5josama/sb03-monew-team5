# sb03-monew-team5
modu's news

## Jacoco 커버리지 확인 기능 추가
./gradlew clean test jacocoTestReport

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

