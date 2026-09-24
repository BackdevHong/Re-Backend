# POST API 모듈

[Chapter 2 모듈 목차](../README.md)

POST 요청의 JSON 본문을 Map과 DTO로 받는 방법을 학습하는 Spring Boot 모듈입니다.
`@RequestBody`를 통한 본문 변환과 `@JsonProperty`를 통한 JSON 필드 이름 매핑을 다룹니다.

## 개발 환경

- 실행 클래스: `org.honginsung.post.PostApplication`
- 기본 포트: `8081` (`get-api`의 기본 포트 `8080`과 함께 실행 가능)
- 공통 환경: JDK 25, Spring Boot 4.1.1, Gradle Wrapper 9.7.1
- 의존성: Spring MVC 및 테스트용 스타터

## 모듈 구조

```text
post-api/
├── README.md
├── build.gradle
└── src/main/
    ├── java/org/honginsung/post/
    │   ├── PostApplication.java
    │   ├── controller/
    │   │   └── PostApiController.java
    │   └── dto/
    │       └── PostRequestDto.java
    └── resources/
        └── application.properties
```

## 학습 내용

### 1. POST 요청과 `@RequestBody`

`PostApiController`는 `@RestController`로 등록되고, `@RequestMapping("/api")`로 공통 경로를 지정합니다.
각 메서드의 `@PostMapping`이 POST 요청을 연결하며, `@RequestBody`는 요청 본문의 JSON을 자바 객체로 변환합니다.
요청 시 `Content-Type: application/json` 헤더를 사용합니다.

### 2. Map으로 요청 본문 받기

`POST /api/post`는 `@RequestBody Map<String, Object>`로 JSON 객체를 받습니다.
필드를 미리 선언하지 않고 전달된 키와 값을 순회하며 서버 콘솔에 `키 : 값`을 출력합니다.
값 타입을 `Object`로 선언해 문자열과 숫자 등 서로 다른 JSON 값을 받을 수 있습니다.

### 3. DTO로 요청 본문 받기

`POST /api/post-fix`는 `@RequestBody PostRequestDto`로 요청을 받습니다.
DTO에 선언된 속성에 값을 매핑한 뒤, `toString()` 결과를 서버 콘솔에 출력합니다.

| JSON 필드 | DTO 필드 | 타입 | 매핑 |
| --- | --- | --- | --- |
| `account` | `account` | `String` | 같은 이름으로 매핑 |
| `email` | `email` | `String` | 같은 이름으로 매핑 |
| `address` | `address` | `String` | 같은 이름으로 매핑 |
| `password` | `password` | `String` | 같은 이름으로 매핑 |
| `phone_number` | `phoneNumber` | `String` | `@JsonProperty("phone_number")` |
| `OTP` | `OTP` | `String` | `@JsonProperty("OTP")` |

`@JsonProperty`로 JSON에서 사용하는 이름을 명시할 수 있습니다. 이 예제는 snake_case인 `phone_number`와 대문자인 `OTP`를 지정해 매핑합니다.
현재 DTO에는 필수값이나 형식 검증을 선언하지 않았으며, 누락된 문자열 속성은 `null`로 남습니다.

## API 목록

| HTTP 메서드 | 경로 | 요청 본문 | 정상 응답 |
| --- | --- | --- | --- |
| POST | `/api/post` | JSON 객체 → `Map<String, Object>` | `200 OK`, 빈 본문 |
| POST | `/api/post-fix` | JSON 객체 → `PostRequestDto` | `200 OK`, 빈 본문 |

두 메서드는 반환 타입이 `void`입니다. 입력 내용은 서버 콘솔에서 확인하며, 저장이나 응답 DTO 반환은 구현하지 않았습니다.

## 실행 및 빌드

저장소 루트에서 다음 명령을 실행합니다.

```bash
cd ch2-spring-boot
./gradlew :post-api:build
./gradlew :post-api:bootRun
```

서버는 `http://localhost:8081`에서 시작합니다. 종료는 실행 터미널에서 `Ctrl+C`를 누릅니다.
빌드한 JAR로도 실행할 수 있습니다.

```bash
java -jar post-api/build/libs/post-api-0.0.1-SNAPSHOT.jar
```

현재 테스트 소스는 없습니다. IntelliJ IDEA에서 ch2 Gradle 프로젝트를 새로고침하면 `post-api` 모듈이 나타나며, `PostApplication.main()`으로 실행할 수 있습니다.

## 요청 예시

서버 실행 후 별도 터미널에서 아래 명령을 실행합니다. 예시 값은 학습용 데이터입니다.

### Map 바인딩

```bash
curl -i -X POST 'http://localhost:8081/api/post' \
  -H 'Content-Type: application/json' \
  -d '{"account":"hong","age":20}'
```

응답은 `200 OK`이며 본문은 비어 있습니다. 서버 콘솔에는 다음과 같이 출력됩니다.

```text
account : hong
age : 20
```

### DTO 및 JSON 필드 이름 매핑

```bash
curl -i -X POST 'http://localhost:8081/api/post-fix' \
  -H 'Content-Type: application/json' \
  -d '{
    "account": "hong",
    "email": "hong@example.com",
    "address": "Seoul",
    "password": "sample-password",
    "phone_number": "010-0000-0000",
    "OTP": "123456"
  }'
```

응답은 `200 OK`이며 본문은 비어 있습니다. 서버 콘솔에서 DTO로 변환된 값을 확인합니다.

```text
PostRequestDto{account='hong', email='hong@example.com', address='Seoul', password='sample-password', phoneNumber='010-0000-0000', OTP='123456'}
```
