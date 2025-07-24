# Spring Boot Auth Service with Email Verification

## 주요 기능
- 회원가입 (비밀번호 암호화, 이메일 인증 토큰 생성 및 메일 발송)
- 로그인 (이메일 인증 여부 확인, JWT Access + Refresh Token 발급)
- 이메일 인증 API (토큰 검증 및 사용자 활성화)
- JWT 인증 필터 및 보안 설정
- H2 인메모리 DB 및 Gmail SMTP 설정 포함

## 실행 방법
1. `application.yml`에 Gmail SMTP 설정 변경 (username, password) => Mailtrap으로 테스트중 username password 따로 구해야함
2. JDK 17 이상 설치
3. 프로젝트 빌드 및 실행 (`./gradlew bootRun` or IDE 실행)
4. Postman 등으로 API 테스트

## API 엔드포인트
- POST /auth/register : 회원가입 요청
- POST /auth/login : 로그인 요청
- GET /auth/verify-email?token= : 이메일 인증 요청

## 서버
- auth-test 에서 ./gradlew bootRun
- cd auth-frontend 이동 후 npm start
- 자바 버전 관련 오류 발생 시 sdk install java 17.0.10-tem
