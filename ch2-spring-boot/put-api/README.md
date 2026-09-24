# PUT API 모듈

[Chapter 2 모듈 목차](../README.md)

PUT 요청의 JSON 본문을 중첩 DTO로 받고, 같은 객체를 JSON 응답으로 반환하는 방법을 학습합니다.
`@JsonNaming`을 통한 snake_case 변환과 `@PathVariable`을 함께 사용하는 예제를 포함합니다.

## 개발 환경

- 실행 클래스: `org.honginsung.put.PutApplication`
- 기본 포트: `8082`
- 공통 환경: JDK 25, Spring Boot 4.1.1, Gradle Wrapper 9.7.1
- 의존성: Spring MVC 및 테스트용 스타터

## 모듈 구조

```text
put-api/
├── README.md
├── build.gradle
└── src/main/
    ├── java/org/honginsung/put/
    │   ├── PutApplication.java
    │   ├── controller/
    │   │   └── PutApiController.java
    │   └── dto/
    │       ├── PostRequestDto.java
    │       └── CarDto.java
    └── resources/
        └── application.properties
```

## 학습 내용

### 1. PUT 요청과 JSON 응답

`PutApiController`의 공통 경로는 `/api`입니다. `@PutMapping`으로 PUT 요청을 연결하고, `@RequestBody`로 JSON 본문을 DTO로 변환합니다.
두 메서드는 받은 DTO를 그대로 반환하므로 응답 본문에 JSON 객체가 담깁니다.
현재 예제는 요청·응답 변환을 보여주며 데이터베이스 저장이나 실제 데이터 갱신은 수행하지 않습니다.

### 2. 중첩 DTO와 배열

현재 PUT 모듈의 요청 클래스 이름은 `PostRequestDto`입니다. POST 모듈의 동명 DTO와는 패키지가 다른 별도 클래스입니다.

| JSON 필드 | Java 필드 | 타입 |
| --- | --- | --- |
| `name` | `PostRequestDto.name` | `String` |
| `age` | `PostRequestDto.age` | `int` |
| `car_list` | `PostRequestDto.carList` | `List<CarDto>` |
| `car_list[].name` | `CarDto.name` | `String` |
| `car_list[].car_number` | `CarDto.carNumber` | `String` |

JSON 배열 `car_list`의 각 객체가 `CarDto`로 변환되고, 리스트 전체가 `PostRequestDto`에 담깁니다.
현재 필수값 검증은 없으며 생략한 참조형 속성은 `null`, `age`는 `0`으로 남습니다.

### 3. `@JsonNaming`으로 이름 변환

`PostRequestDto`와 `CarDto` 모두 클래스에 `@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)`를 적용합니다.
따라서 `carList ↔ car_list`, `carNumber ↔ car_number` 변환이 요청과 응답에 모두 적용됩니다.
상위 DTO와 중첩 DTO 각각에 명명 전략을 선언한 구조입니다.

이 프로젝트는 `tools.jackson.databind`의 `PropertyNamingStrategies`와 `tools.jackson.databind.annotation.JsonNaming`을 사용합니다.
`carList` 위의 `@JsonProperty("car_list")`는 주석 처리되어 있으며, 현재 동작은 클래스의 명명 전략으로 처리합니다.

### 4. 경로 변수와 요청 본문 함께 받기

`PUT /api/put/{userId}`는 `@PathVariable Long userId`와 `@RequestBody PostRequestDto`를 동시에 받습니다.
`userId`는 서버 콘솔에 출력하고, 응답에는 요청 DTO만 반환합니다. 예를 들어 `/api/put/1`의 `1`은 응답 필드로 추가되지 않습니다.

## API 목록

| HTTP 메서드 | 경로 | 정상 응답 | 서버 콘솔 출력 |
| --- | --- | --- | --- |
| PUT | `/api/put` | `200 OK`, 요청 DTO의 JSON 표현 | 요청 DTO |
| PUT | `/api/put/{userId}` | `200 OK`, 요청 DTO의 JSON 표현 | `userId` |

## 실행 및 빌드

저장소 루트에서 다음 명령을 실행합니다.

```bash
cd ch2-spring-boot
./gradlew :put-api:build
./gradlew :put-api:bootRun
```

서버는 `http://localhost:8082`에서 시작합니다. 종료는 실행 터미널에서 `Ctrl+C`를 누릅니다.
빌드한 JAR로도 실행할 수 있습니다.

```bash
java -jar put-api/build/libs/put-api-0.0.1-SNAPSHOT.jar
```

현재 테스트 소스는 없습니다. IntelliJ IDEA에서 ch2 Gradle 프로젝트를 새로고침하고 `PutApplication.main()`으로 실행할 수 있습니다.

## 요청·응답 예시

서버 실행 후 별도 터미널에서 요청합니다.

```bash
curl -i -X PUT 'http://localhost:8082/api/put' \
  -H 'Content-Type: application/json' \
  -d '{
    "name": "honginsung",
    "age": 22,
    "car_list": [
      {"name": "BMW", "car_number": "11가 1234"},
      {"name": "AUDI", "car_number": "22가 0923"}
    ]
  }'
```

응답은 `200 OK`이며, 본문은 다음과 같은 JSON입니다. 필드 출력 순서와 공백은 달라질 수 있습니다.

```json
{
  "name": "honginsung",
  "age": 22,
  "car_list": [
    {"name": "BMW", "car_number": "11가 1234"},
    {"name": "AUDI", "car_number": "22가 0923"}
  ]
}
```

경로 변수를 함께 전달하는 요청:

```bash
curl -i -X PUT 'http://localhost:8082/api/put/1' \
  -H 'Content-Type: application/json' \
  -d '{"name":"honginsung","age":22,"car_list":[]}'
```

서버 콘솔에는 `1`이 출력되고, 응답 본문은 다음과 같습니다.

```json
{"name":"honginsung","age":22,"car_list":[]}
```

`userId`에 숫자로 변환할 수 없는 값을 전달하거나 JSON 문법이 잘못되면 `400 Bad Request`가 반환됩니다.
