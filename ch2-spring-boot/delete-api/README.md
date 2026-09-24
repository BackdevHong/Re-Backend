# DELETE API 모듈

[Chapter 2 모듈 목차](../README.md)

DELETE 요청에서 경로 변수와 쿼리 파라미터를 함께 받는 방법을 학습하는 Spring Boot 모듈입니다.
현재 예제는 전달받은 값을 서버 콘솔에 출력하며, 실제 데이터 삭제나 데이터베이스 처리는 수행하지 않습니다.

## 개발 환경

- 실행 클래스: `org.honginsung.delete.DeleteApplication`
- 기본 포트: `8083`
- 공통 환경: JDK 25, Spring Boot 4.1.1, Gradle Wrapper 9.7.1
- 의존성: Spring MVC 및 테스트용 스타터

## 모듈 구조

```text
delete-api/
├── README.md
├── build.gradle
└── src/main/
    ├── java/org/honginsung/delete/
    │   ├── DeleteApplication.java
    │   └── controller/
    │       └── DeleteApiController.java
    └── resources/
        └── application.properties
```

## 학습 내용

`DeleteApiController`는 `@RestController`로 등록하고 `@RequestMapping("/api")`로 공통 경로를 지정합니다.

- `@DeleteMapping("/delete/{userId}")`: DELETE 요청을 처리하는 메서드를 연결합니다.
- `@PathVariable String userId`: URL 경로의 `{userId}`를 문자열로 받습니다. 숫자로 변환하지 않으므로 `user-1` 같은 문자열도 사용할 수 있습니다.
- `@RequestParam String account`: 쿼리 파라미터 `account`를 받습니다. 현재 필수 인자이므로 누락하면 `400 Bad Request`가 반환됩니다.
- 메서드는 `userId`, `account`를 순서대로 서버 콘솔에 출력합니다.

요청 본문을 받는 `@RequestBody`는 사용하지 않습니다. 반환 타입이 `void`이므로 정상 응답은 `200 OK`이며 본문은 비어 있습니다.

## API 목록

| HTTP 메서드 | 경로 | 쿼리 파라미터 | 정상 응답 |
| --- | --- | --- | --- |
| DELETE | `/api/delete/{userId}` | `account` (필수) | `200 OK`, 빈 본문 |

## 실행 및 빌드

저장소 루트에서 다음 명령을 실행합니다.

```bash
cd ch2-spring-boot
./gradlew :delete-api:build
./gradlew :delete-api:bootRun
```

서버는 `http://localhost:8083`에서 시작합니다. 종료는 실행 터미널에서 `Ctrl+C`를 누릅니다.
빌드한 JAR로도 실행할 수 있습니다.

```bash
java -jar delete-api/build/libs/delete-api-0.0.1-SNAPSHOT.jar
```

현재 테스트 소스는 없습니다. IntelliJ IDEA에서 ch2 Gradle 프로젝트를 새로고침한 뒤 `DeleteApplication.main()`으로 실행할 수 있습니다.

## 요청 예시

서버 실행 후 별도 터미널에서 요청합니다.

```bash
curl -i -X DELETE 'http://localhost:8083/api/delete/user-1?account=hong'
```

응답 상태는 `200 OK`이고 본문은 비어 있습니다. 서버 콘솔에는 다음 값이 출력됩니다.

```text
user-1
hong
```

필수 쿼리 파라미터 `account`를 생략하면 `400 Bad Request`가 반환됩니다.

```bash
curl -i -X DELETE 'http://localhost:8083/api/delete/user-1'
```
