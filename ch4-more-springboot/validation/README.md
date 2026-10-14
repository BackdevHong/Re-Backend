# Validation — 요청 데이터 검증

[Chapter 4 모듈 목차](../README.md) · [전체 챕터 목차](../../README.md)

Jakarta Bean Validation으로 요청 DTO의 값과 중첩 객체를 검증하는 예제입니다.
`@Valid`로 검증을 실행하고, `BindingResult`에서 오류를 확인해 정상 요청은 `200`, 검증 실패는 `400`으로 응답합니다.

## 개발 환경

- Java 25 / Spring Boot 4.1.1 / Gradle Wrapper 9.7.1
- 의존성: `spring-boot-starter-webmvc`, `spring-boot-starter-validation`
- 실행 클래스: `org.honginsung.validation.ValidationApplication`
- 애플리케이션 이름: `validation`, 기본 포트: `8080`

## 모듈 구조

```text
validation/
├── README.md
├── build.gradle
└── src/
    ├── main/
    │   ├── java/org/honginsung/validation/
    │   │   ├── ValidationApplication.java
    │   │   ├── controller/ApiController.java
    │   │   ├── dto/                  # 기본 제약 조건과 사용자 정의 검증 예제
    │   │   │   ├── User.java
    │   │   │   └── Car.java
    │   │   ├── dto2/                 # 현재 API가 사용하는 중첩 객체 검증 예제
    │   │   │   ├── User.java
    │   │   │   └── Car.java
    │   │   ├── annotation/YearMonth.java
    │   │   └── validator/YearMonthValidator.java
    │   └── resources/application.properties
    └── test/java/org/honginsung/validation/ValidationApplicationTests.java
```

## 현재 API: 중첩 객체 검증

`POST /api/user`는 **`dto2.User`**를 요청과 응답 타입으로 사용합니다.

| 대상 | 제약 조건 | 동작 |
| --- | --- | --- |
| `User.name` | `@NotBlank` | null, 빈 문자열, 공백 문자열 거부 |
| `User.age` | `@Max(90)` | 90 초과 거부 |
| `User.cars` | `@Valid` | 목록 내부 `Car` 객체의 제약 조건도 검증 |
| `Car.name` | `@NotBlank` | 자동차 이름 필수 |
| `Car.carNumber` | `@NotBlank`, `@JsonProperty("car_number")` | 자동차 번호 필수, JSON 이름은 `car_number` |
| `Car.type` | `@NotBlank`, `@JsonProperty("TYPE")` | 종류 필수, JSON 이름은 대문자 `TYPE` |

### 검증과 응답 흐름

1. JSON 요청 본문을 `dto2.User`로 변환합니다.
2. 컨트롤러 매개변수의 `@Valid`로 사용자 제약 조건을 검사합니다.
3. `cars`의 `@Valid`를 통해 각 자동차의 제약 조건까지 검사합니다.
4. 요청 객체 바로 다음에 선언한 `BindingResult`에서 오류를 확인합니다.
5. 오류가 있으면 필드 경로와 메시지를 연결한 문자열을 `400 Bad Request`로 반환합니다.
6. 오류가 없으면 사용자 객체를 출력하고 같은 객체를 JSON으로 반환합니다.

오류 본문은 JSON이 아닌 문자열이며, 현재 구현은 필드명과 메시지 사이에 구분자를 넣지 않습니다. 여러 오류도 줄바꿈 없이 이어 붙이며 순서를 보장하지 않습니다.
중첩 필드 오류의 경로는 JSON 속성명이 아닌 Java 필드명 기준이므로 `cars[0].carNumber`처럼 표시됩니다.

### 현재 검증 범위

- `age`에는 최솟값 제약이 없어 음수도 통과합니다. 나이가 생략되면 `int` 기본값인 `0`을 사용합니다.
- `cars`에는 필수·크기 제약이 없어 누락, null, 빈 배열도 통과합니다. `@Valid`는 목록 자체의 존재나 최소 개수를 보장하지 않습니다.
- 현재 오류 처리 코드는 모든 오류를 `FieldError`로 가정합니다. 클래스 수준 제약 조건을 추가할 때는 객체 수준 오류 처리도 고려해야 합니다.
- 실행 시 현재 검증 라이브러리는 컬렉션 필드의 `@Valid` 대신 `List<@Valid Car>` 형태로 원소 타입에 적용하도록 권장하는 경고를 출력합니다. 현재 예제의 중첩 검증은 동작합니다.

## 기본 제약 조건 예제: dto 패키지

`dto.User`는 기본 검증 애너테이션과 사용자 정의 검증을 학습하기 위한 별도 DTO입니다. 현재 컨트롤러는 이 클래스를 import하지 않으므로 아래 규칙은 현재 API에 적용되지 않습니다.

| 필드 | 제약 조건 | 내용 |
| --- | --- | --- |
| `name` | `@NotBlank` | 이름 필수 |
| `age` | `@Min(0)`, `@Max(90)` | 0 이상 90 이하 |
| `email` | `@Email` | 이메일 형식 검증 |
| `phoneNumber` | `@Pattern` | 숫자 2~3자리, 3~4자리, 4자리를 하이픈으로 연결 |
| `reqYearMonth` | `@YearMonth` | 사용자 정의 연월 검증 |

전화번호의 실제 정규식은 `^\d{2,3}-\d{3,4}-\d{4}$`입니다. 오류 메시지에는 `01x`가 적혀 있지만 정규식은 첫 구간을 `01`로 제한하지 않습니다.
`@Email`, `@Pattern`만으로 필수 입력을 보장하지는 않으며, 현재 두 필드에는 `@NotBlank`가 없습니다.
`dto.Car`는 이름에만 `@NotBlank`를 적용한 이전 예제로, 현재 `dto.User`에는 자동차 목록 필드가 없습니다.

## 사용자 정의 @YearMonth

`YearMonth` 애너테이션은 `@Constraint(validatedBy = YearMonthValidator.class)`로 검증기를 연결하고 런타임까지 유지됩니다.
기본 오류 메시지는 `yyyyMM 형식에 맞지 않습니다.`입니다.

검증기는 다음 순서로 동작합니다.

1. `initialize()`에서 애너테이션의 `pattern`을 읽습니다. 기본 패턴은 `yyyyMMdd`입니다.
2. `isValid()`에서 입력 문자열 뒤에 `"01"`을 붙여 해당 월의 1일로 만듭니다.
3. `LocalDate.parse()`가 성공하면 `true`, 예외가 발생하면 `false`를 반환합니다.

예를 들어 `202610`을 전달하면 `20261001`을 파싱합니다. 입력 형식은 연월이고, 기본 파싱 패턴은 일이 추가된 날짜라는 차이가 있습니다.
이 구현은 별도의 길이 정규식이나 엄격한 날짜 파싱 설정을 사용하지 않습니다. 현재 API에서는 이 검증기를 호출하지 않으며, 직접 사용하려면 해당 제약이 붙은 DTO를 검증 대상으로 연결해야 합니다.

## 실행과 요청 예제

저장소 루트에서 실행합니다.

```bash
cd ch4-more-springboot
./gradlew :validation:bootRun
```

### 정상 요청

```bash
curl -i -X POST 'http://localhost:8080/api/user' \
  -H 'Content-Type: application/json' \
  -d '{"name":"Hong","age":20,"cars":[{"name":"K5","car_number":"11가 1234","TYPE":"sedan"}]}'
```

`200 OK`와 함께 요청과 같은 필드 값을 반환합니다. JSON 필드 순서는 달라질 수 있습니다.

```json
{
  "name": "Hong",
  "age": 20,
  "cars": [{"name": "K5", "car_number": "11가 1234", "TYPE": "sedan"}]
}
```

### 사용자 필드 오류

```bash
curl -i -X POST 'http://localhost:8080/api/user' \
  -H 'Content-Type: application/json' \
  -H 'Accept-Language: en' \
  -d '{"name":"Hong","age":91,"cars":[]}'
```

`400 Bad Request`의 본문 예:

```text
field : agemust be less than or equal to 90
```

### 자동차 필드 오류

```bash
curl -i -X POST 'http://localhost:8080/api/user' \
  -H 'Content-Type: application/json' \
  -H 'Accept-Language: en' \
  -d '{"name":"Hong","age":20,"cars":[{"name":"K5","car_number":"","TYPE":"sedan"}]}'
```

`400 Bad Request`의 본문 예:

```text
field : cars[0].carNumbermust not be blank
```

기본 제약 조건의 오류 메시지는 로케일에 따라 달라질 수 있습니다.

## 빌드와 검증

`ch4-more-springboot/`에서 실행합니다.

```bash
./gradlew :validation:build
./gradlew :validation:test
```

자동 테스트는 `ValidationApplicationTests.contextLoads()` 1개이며, 스프링 컨텍스트 로딩을 확인합니다. API 검증 결과에 대한 자동 단언문은 아직 없습니다.

문서 정리 시 별도로 서버를 실행해 정상 요청, 나이 90, 공백 이름, 나이 91, 자동차의 세 필드에 대한 빈 값, 음수 나이, 빈 자동차 목록, 자동차 목록 생략 등 HTTP 요청 10건을 확인했습니다. 사용자 정의 `@YearMonth`는 해당 API 검증에 포함되지 않습니다.
