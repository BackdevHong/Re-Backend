# Response API 모듈

[Chapter 2 모듈 목차](../README.md)

텍스트, JSON, HTML 응답과 HTTP 상태 코드를 다루는 Spring Boot 모듈입니다.
`@RestController`, `@Controller`, `@ResponseBody`, `ResponseEntity`의 역할과 DTO 직렬화 설정을 학습합니다.

## 개발 환경

- 실행 클래스: `org.honginsung.response.ResponseApplication`
- 기본 포트: `8084`
- 공통 환경: JDK 25, Spring Boot 4.1.1, Gradle Wrapper 9.7.1
- 의존성: Spring MVC 및 테스트용 스타터

## 모듈 구조

```text
response-api/
├── README.md
├── build.gradle
└── src/main/
    ├── java/org/honginsung/response/
    │   ├── ResponseApplication.java
    │   ├── controller/
    │   │   ├── ApiController.java
    │   │   └── PageController.java
    │   └── dto/
    │       └── User.java
    └── resources/
        ├── application.properties
        └── static/
            └── main.html
```

## 학습 내용

### 1. 텍스트와 JSON 응답

`ApiController`는 `@RestController`와 `@RequestMapping("/api")`를 사용합니다.

- `GET /api/text`: 필수 쿼리 파라미터 `account`를 받아 같은 문자열을 응답 본문으로 반환합니다.
- `POST /api/json`: `@RequestBody`로 JSON을 `User` 객체로 변환한 뒤, 같은 객체를 반환합니다. 응답 시 다시 JSON으로 직렬화됩니다.

문자열 반환은 `text/plain`, DTO 반환은 `application/json`으로 응답합니다. DTO를 반환하는 예제는 요청·응답 변환을 보여주며 데이터 저장은 수행하지 않습니다.

### 2. `ResponseEntity`로 상태 코드 지정

`PUT /api/put`은 `ResponseEntity<User>`를 반환합니다.
`ResponseEntity.status(HttpStatus.CREATED).body(user)`로 상태 코드를 `201 Created`로 지정하고, 본문에는 전달받은 `User`를 JSON으로 담습니다.
현재 코드는 응답 형식을 학습하는 예제이며 실제 리소스를 생성하거나 갱신하지 않습니다.

### 3. HTML 화면과 `@ResponseBody`

`PageController`는 `@Controller`를 사용합니다.

- `/main`의 `main()`은 `main.html`이라는 뷰 이름을 반환합니다. 현재 설정에서 `static/main.html`의 HTML 화면이 표시됩니다.
- `/main.html`로 정적 파일에 직접 접근할 수도 있습니다.
- `GET /user`의 `user()`에는 `@ResponseBody`를 붙여 반환한 `User`를 JSON 응답 본문으로 내보냅니다.

`@Controller`에서 뷰 이름을 반환하는 방식과, `@ResponseBody`를 붙여 값을 본문으로 반환하는 방식을 비교할 수 있습니다. `@RestController`는 컨트롤러 전체에 응답 본문 반환 방식을 적용합니다.

### 4. JSON 필드 이름과 null 제외

`User`는 다음 속성을 갖습니다.

| Java 필드 | 타입 | JSON 필드 |
| --- | --- | --- |
| `name` | `String` | `name` |
| `age` | `Integer` | `age` |
| `phoneNumber` | `String` | `phone_number` |
| `address` | `String` | `address` |

- `@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)`로 `phoneNumber ↔ phone_number` 변환을 적용합니다. 요청 JSON에도 `phone_number`를 사용합니다.
- `@JsonInclude(JsonInclude.Include.NON_NULL)`로 응답에서 값이 `null`인 필드를 제외합니다.
- `age`는 `Integer`이므로 설정하지 않으면 `null`입니다. `0`을 명시하면 응답에 `"age": 0`이 포함됩니다.

`GET /user`는 이름·전화번호·주소만 설정하므로 응답에 `age`가 없습니다. DTO에는 필수값 검증을 선언하지 않았으며, JSON 필드 순서와 출력 공백은 달라질 수 있습니다.

## API와 화면 목록

| 요청 | 입력 | 정상 응답 | 응답 형식 |
| --- | --- | --- | --- |
| `GET /api/text` | 쿼리 파라미터 `account` (필수) | `200 OK`, account 값 | 텍스트 |
| `POST /api/json` | `User` JSON 본문 | `200 OK`, User | JSON |
| `PUT /api/put` | `User` JSON 본문 | `201 Created`, User | JSON |
| `GET /main` | 없음 | `200 OK`, main.html 화면 | HTML |
| `GET /main.html` | 없음 | `200 OK`, 정적 파일 | HTML |
| `GET /user` | 없음 | `200 OK`, 코드에서 생성한 User | JSON |

`/main`은 현재 `@RequestMapping`에 HTTP 메서드를 제한하지 않았으며, 위 표와 실행 예시는 브라우저의 GET 요청 기준입니다.

## 실행 및 빌드

저장소 루트에서 다음 명령을 실행합니다.

```bash
cd ch2-spring-boot
./gradlew :response-api:build
./gradlew :response-api:bootRun
```

기본 주소는 `http://localhost:8084`입니다. 서버 종료는 실행 터미널에서 `Ctrl+C`를 누릅니다.
빌드한 JAR로도 실행할 수 있습니다.

```bash
java -jar response-api/build/libs/response-api-0.0.1-SNAPSHOT.jar
```

현재 테스트 소스는 없습니다. IntelliJ IDEA에서 ch2 Gradle 프로젝트를 새로고침하고 `ResponseApplication.main()`으로 실행할 수 있습니다.

## 요청·응답 예시

### 텍스트

```bash
curl -i 'http://localhost:8084/api/text?account=hong'
```

상태는 `200 OK`, 본문은 `hong`입니다. `account`를 생략하면 `400 Bad Request`가 반환됩니다.

### JSON과 상태 코드

```bash
curl -i -X POST 'http://localhost:8084/api/json' \
  -H 'Content-Type: application/json' \
  -d '{"name":"hong","age":22,"phone_number":"010-0000-0000","address":"Seoul"}'
```

상태는 `200 OK`이며, 응답 본문은 다음과 같습니다.

```json
{"name":"hong","age":22,"phone_number":"010-0000-0000","address":"Seoul"}
```

같은 데이터를 PUT 예제로 보내면 `201 Created`와 JSON 본문을 받습니다.

```bash
curl -i -X PUT 'http://localhost:8084/api/put' \
  -H 'Content-Type: application/json' \
  -d '{"name":"hong","age":22,"phone_number":"010-0000-0000","address":"Seoul"}'
```

null 필드 제외를 확인하는 요청:

```bash
curl -i -X POST 'http://localhost:8084/api/json' \
  -H 'Content-Type: application/json' \
  -d '{"name":"hong","age":null}'
```

```json
{"name":"hong"}
```

### 컨트롤러의 JSON 응답

```bash
curl -i http://localhost:8084/user
```

```json
{"name":"HongInSung","phone_number":"010-2341-4532","address":"FastCampus"}
```

### HTML 화면

브라우저에서 [메인 화면](http://localhost:8084/main)을 열면 `Main Html Spring Body` 문구가 표시됩니다. 터미널에서도 HTML 응답을 확인할 수 있습니다.

```bash
curl -i http://localhost:8084/main
curl -i http://localhost:8084/main.html
```
