# Chapter 3. AOP — 공통 기능 분리

[전체 챕터 목차](../README.md) · [DI 예제](../ch3-di/README.md) · [IoC 예제](../ch3-ioc/README.md)

컨트롤러에 반복해서 작성할 기능을 Aspect로 분리하는 학습 예제입니다.
실무에서 활용하는 요청·응답 로깅, 실행 시간 측정, 요청·응답 데이터 변환을 간단한 REST API에 적용합니다.

## 개발 환경

- Java 25 (Gradle Toolchain)
- Spring Boot 4.1.1 / Gradle Wrapper 9.7.1
- `spring-boot-starter-webmvc`, `spring-boot-starter-aspectj`
- Chapter 2의 멀티 모듈 빌드와 분리된 독립 Gradle 프로젝트
- 기본 포트: `8080`

Spring Boot 4에서는 기존 `spring-boot-starter-aop`의 이름이 `spring-boot-starter-aspectj`로 변경되었습니다. 이 프로젝트도 변경된 의존성을 사용합니다.
참고: [Spring Boot 4 마이그레이션 가이드](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-4.0-Migration-Guide).

## 프로젝트 구조

```text
ch3-aop/
├── README.md
├── build.gradle
├── settings.gradle
├── gradlew
├── gradle/wrapper/
└── src/
    ├── main/
    │   ├── java/org/honginsung/aop/
    │   │   ├── AopApplication.java
    │   │   ├── annotation/
    │   │   │   ├── Timer.java              # 실행 시간 측정 표시
    │   │   │   └── Decode.java             # 이메일 변환 표시
    │   │   ├── aop/
    │   │   │   ├── ParameterAop.java       # 메서드 인자·반환값 출력
    │   │   │   ├── TimerAop.java           # StopWatch로 실행 시간 측정
    │   │   │   └── DecodeAop.java          # 이메일 Base64 변환
    │   │   ├── controller/RestApiController.java
    │   │   └── dto/User.java
    │   └── resources/application.properties
    └── test/java/org/honginsung/aop/AopApplicationTests.java
```

## AOP 구성

AOP(Aspect-Oriented Programming)는 여러 곳에 반복되는 공통 관심사를 별도 코드로 분리하는 방식입니다. 이 예제에서는 스프링이 관리하는 컨트롤러의 메서드 호출에 부가 기능을 적용합니다.

| 요소 | 예제에서의 역할 |
| --- | --- |
| Aspect | `@Aspect`와 `@Component`로 등록한 공통 기능 클래스 |
| Join point | 공통 기능을 적용할 수 있는 메서드 실행 지점 |
| Pointcut | 공통 기능을 적용할 메서드를 선택하는 조건 |
| Advice | 메서드 실행 전·후 또는 실행을 감싸며 수행하는 코드 |
| `JoinPoint` | 호출된 메서드와 인자 정보 조회 |
| `ProceedingJoinPoint` | `@Around`에서 `proceed()`로 원래 호출 흐름 진행 |

세 Aspect는 다음 포인트컷으로 `controller` 패키지와 하위 패키지의 메서드를 선택합니다.

```text
execution(* org.honginsung.aop.controller..*.*(..))
```

`TimerAop`, `DecodeAop`는 여기에 `@annotation(...)` 조건을 `&&`로 연결합니다. 따라서 컨트롤러 메서드 중 해당 애너테이션을 붙인 메서드에만 동작합니다.
`@Timer`, `@Decode`는 런타임에 유지되며, 선언상 클래스에도 붙일 수 있지만 현재 포인트컷은 메서드에 붙은 애너테이션을 대상으로 합니다.

## 실무 활용 사례와 구현

### 1. 요청·응답 공통 로깅 — ParameterAop

컨트롤러마다 로그 코드를 반복하지 않고 호출 정보를 한곳에서 확인하는 예제입니다.

- `@Before`: 메서드 이름, 인자의 타입과 값을 출력합니다.
- `@AfterReturning`: 정상적으로 반환한 값을 출력합니다.
- 적용 범위: 포인트컷에 해당하는 모든 컨트롤러 메서드

현재는 `System.out.println()`과 DTO의 `toString()`으로 출력합니다. HTTP 헤더나 직렬화된 응답 본문을 기록하는 필터가 아니라, 메서드의 인자와 반환 객체를 기록합니다.

### 2. 처리 시간 측정 — TimerAop

느린 작업의 처리 시간을 확인하는 예제입니다. `@Timer`가 붙은 메서드에서 `@Around` Advice가 다음 순서로 실행됩니다.

1. `StopWatch`를 시작합니다.
2. `joinPoint.proceed()`로 원래 메서드를 실행합니다.
3. 측정을 종료하고 경과 시간을 초 단위로 출력합니다.

`DELETE /api/delete`는 데이터베이스 작업을 가정해 `Thread.sleep(1000 * 2)`로 2초간 대기합니다. 실제 삭제나 데이터베이스 연결은 수행하지 않습니다.

```text
total time : 2.005094542 seconds
```

실행 시간은 환경마다 달라집니다. 현재 `@Around` 메서드는 `void`를 반환하며 반환값이 없는 DELETE 예제에 사용합니다. 반환값이 있는 메서드에 확장하려면 `proceed()`의 결과를 반환해야 하며, 예외 발생 시에도 시간을 기록하려면 `finally`에서 측정을 마무리하도록 보완해야 합니다.

### 3. 요청·응답 데이터 변환 — DecodeAop

외부 전달 형식과 내부 처리 형식을 분리하는 예제입니다. `@Decode`를 붙인 `PUT /api/put`에서 `User.email`을 변환합니다.

```text
요청 JSON의 Base64 이메일
    → @Before: 디코딩 후 User.email 변경
    → 컨트롤러: 일반 이메일 문자열 사용
    → @AfterReturning: 반환 User.email을 Base64로 재인코딩
    → 응답 JSON
```

예를 들어 요청의 `c3RldmVAZ21haWwuY29t`는 컨트롤러 안에서 `steve@gmail.com`이 되고, 응답에서는 다시 원래 Base64 문자열이 됩니다. 현재 컨트롤러는 전달받은 `User` 객체를 그대로 반환합니다.

`ParameterAop`도 함께 적용되지만 Aspect 간 실행 순서를 명시하지 않았으므로 공통 로그에 출력되는 이메일의 변환 전·후 상태에 의존해서는 안 됩니다.

### 현재 예제의 범위

- Base64는 인코딩이며 암호화가 아닙니다.
- `User.toString()`에는 `pw`와 이메일이 포함되어 현재 로그에 그대로 출력됩니다. 실제 요청 로깅에서는 비밀번호를 제외하고 개인정보를 마스킹해야 합니다.
- 이메일의 누락·잘못된 Base64 형식에 대한 검증과 별도 예외 응답은 아직 구현하지 않았습니다.
- `ParameterAop`의 인자 타입 출력은 `null`을 처리하지 않습니다. 적용 대상을 넓힐 때는 `null` 인자 처리가 필요합니다.

## API 목록

| 메서드 | 경로 | 입력 | 정상 응답 | 적용 기능 |
| --- | --- | --- | --- | --- |
| GET | `/api/get/{id}?name=...` | `long id`, 문자열 `name` | `200`, `id name` 문자열 | 인자·반환값 로깅 |
| POST | `/api/post` | `User` JSON | `200`, 입력받은 `User` JSON | 인자·반환값 로깅 |
| DELETE | `/api/delete` | 없음 | `200`, 빈 본문 | 로깅, 실행 시간 측정 |
| PUT | `/api/put` | 이메일을 Base64로 인코딩한 `User` JSON | `200`, 이메일을 재인코딩한 `User` JSON | 로깅, 이메일 변환 |

`User`의 `id`, `pw`, `email`은 모두 문자열입니다. 아래 요청은 학습용 임의 값을 사용합니다.

## 실행 및 요청 예제

저장소 루트에서 실행합니다.

```bash
cd ch3-aop
./gradlew bootRun
```

서버 시작 후 `AopApplication.main()`에서 `steve@gmail.com`을 인코딩한 `c3RldmVAZ21haWwuY29t`도 출력합니다. 서버는 `Ctrl+C`로 종료합니다.

다른 프로젝트가 `8080`을 사용 중이면 포트를 바꿉니다. 이 경우 아래 요청 URL의 포트도 맞춰주세요.

```bash
./gradlew bootRun --args='--server.port=8087'
```

### GET: 인자와 반환값 확인

```bash
curl -i 'http://localhost:8080/api/get/1?name=Hong'
```

응답 본문: `1 Hong`. 서버 콘솔에서 `get`, `Long`·`String` 인자의 값과 반환값을 확인할 수 있습니다.

### POST: 객체 로깅 확인

```bash
curl -i -X POST 'http://localhost:8080/api/post' \
  -H 'Content-Type: application/json' \
  -d '{"id":"hong","pw":"demo","email":"hong@example.com"}'
```

응답 JSON에는 요청과 같은 필드 값이 담깁니다. JSON 필드 순서는 의미가 없습니다.

### DELETE: 실행 시간 확인

```bash
curl -i -X DELETE 'http://localhost:8080/api/delete'
```

약 2초 뒤 빈 본문으로 응답하며, 서버 콘솔에 `total time : ... seconds`가 출력됩니다.

### PUT: 이메일 변환 확인

```bash
curl -i -X PUT 'http://localhost:8080/api/put' \
  -H 'Content-Type: application/json' \
  -d '{"id":"steve","pw":"demo","email":"c3RldmVAZ21haWwuY29t"}'
```

컨트롤러가 출력하는 `User`의 이메일은 `steve@gmail.com`이며, 응답은 다음 값을 포함합니다.

```json
{
  "id": "steve",
  "pw": "demo",
  "email": "c3RldmVAZ21haWwuY29t"
}
```

## 빌드와 검증

`ch3-aop/`에서 실행합니다.

```bash
./gradlew build
./gradlew test
```

현재 자동 테스트는 `AopApplicationTests.contextLoads()` 1개로, 스프링 컨텍스트 초기화를 확인합니다. API 응답이나 Advice의 동작에 대한 단언문은 포함되어 있지 않습니다.

문서 정리 시 실제 서버에 위 네 종류의 요청을 보내 정상 응답을 확인했습니다. 콘솔에서 인자·반환값 로그, PUT 컨트롤러의 디코딩된 이메일과 응답의 재인코딩 결과, DELETE의 약 2초 실행 시간도 확인했습니다.
