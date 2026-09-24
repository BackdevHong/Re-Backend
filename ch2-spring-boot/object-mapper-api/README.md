# ObjectMapper API 모듈

[Chapter 2 모듈 목차](../README.md)

ObjectMapper로 자바 객체를 JSON 문자열로 직렬화하고, JSON 문자열을 다시 객체로 역직렬화하는 방법을 학습합니다.
현재 예제는 HTTP 컨트롤러 대신 `ObjectMapperApplicationTests.contextLoads()`에서 실행합니다.

## 개발 환경

- 실행 클래스: `org.honginsung.objectmapper.ObjectMapperApplication`
- 기본 포트: `8085` (웹 애플리케이션 실행 시)
- 공통 환경: JDK 25, Spring Boot 4.1.1, Gradle Wrapper 9.7.1
- ObjectMapper: `tools.jackson.databind.ObjectMapper` 사용
- 테스트: JUnit, `@SpringBootTest`

## 모듈 구조

```text
object-mapper-api/
├── README.md
├── build.gradle
└── src/
    ├── main/
    │   ├── java/org/honginsung/objectmapper/
    │   │   ├── ObjectMapperApplication.java
    │   │   └── User.java
    │   └── resources/
    │       └── application.properties
    └── test/java/org/honginsung/objectmapper/
        └── ObjectMapperApplicationTests.java
```

## 학습 내용

### 1. 객체를 JSON 문자열로 변환

테스트는 `new ObjectMapper()`로 매퍼를 직접 생성하고, `new User("Hong", 20, "010-1234-1234")`로 변환할 객체를 준비합니다.

`writeValueAsString(user)`는 객체의 JSON 속성을 읽어 문자열로 반환합니다. 현재 `User`의 getter와 `@JsonProperty` 설정에 따라 이름, 나이, 전화번호가 출력됩니다.

```json
{"name":"Hong","age":20,"phone_number":"010-1234-1234"}
```

테스트에서 직접 생성한 매퍼를 사용하므로, 스프링이 자동 구성한 매퍼를 주입받는 예제는 아닙니다.

### 2. JSON 문자열을 객체로 변환

`readValue(text, User.class)`는 앞서 만든 JSON 문자열을 `User` 타입으로 변환합니다.
결과를 출력하면 `User.toString()`을 통해 각 필드 값을 확인할 수 있습니다.

```text
User{name='Hong', age=20, phoneNumber='010-1234-1234'}
```

현재 `User`는 세 인자를 받는 생성자를 제공하며 기본 생성자와 setter는 없습니다. 이 프로젝트의 Jackson 및 컴파일 설정에서는 현재 코드로 역직렬화가 성공합니다. 테스트 소스에 있는 “default 생성자를 필요로 함” 주석은 모든 구성에 적용되는 필수 조건으로 해석하지 않습니다.

### 3. `@JsonProperty`로 필드 이름 지정

| Java 필드 | 타입 | JSON 필드 |
| --- | --- | --- |
| `name` | `String` | `name` |
| `age` | `Integer` | `age` |
| `phoneNumber` | `String` | `phone_number` |

`phoneNumber` 필드의 `@JsonProperty("phone_number")`는 JSON에서 사용할 이름을 지정합니다. 객체를 JSON으로 내보내거나 JSON을 읽을 때 이 이름이 적용됩니다.

### 4. getter와 일반 메서드의 차이

`getName()`, `getAge()`, `getPhoneNumber()`는 JSON 속성을 읽는 getter입니다.
`defaultUser()`는 기본 사용자 객체를 반환하는 일반 메서드로, 현재 직렬화 결과에 `defaultUser` 속성은 포함되지 않습니다.

보조 메서드를 `getDefaultUser()`처럼 getter 형태로 작성하면 JSON 속성으로 인식될 수 있습니다. 특히 같은 타입의 새 객체를 계속 반환하는 getter는 직렬화가 반복되는 원인이 될 수 있습니다. 현재 코드는 일반 메서드 이름인 `defaultUser()`를 사용합니다.

## 테스트 실행

저장소 루트에서 챕터 폴더로 이동해 실행합니다.

```bash
cd ch2-spring-boot

# 모듈 테스트 전체 실행
./gradlew :object-mapper-api:test

# 변환 예제 테스트만 실행
./gradlew :object-mapper-api:test --tests 'org.honginsung.objectmapper.ObjectMapperApplicationTests.contextLoads'

# 테스트를 포함한 빌드
./gradlew :object-mapper-api:build
```

`contextLoads()`는 스프링 컨텍스트를 시작하고 객체→JSON→객체 변환을 실행합니다. 테스트가 수행되면 로그에 다음 내용이 출력됩니다.

```text
--------------
{"name":"Hong","age":20,"phone_number":"010-1234-1234"}
User{name='Hong', age=20, phoneNumber='010-1234-1234'}
```

현재 테스트에는 `assertEquals`나 `assertThat` 같은 값 비교 단언문이 없습니다. 따라서 성공 결과는 컨텍스트 시작과 변환 과정에서 예외가 발생하지 않았음을 의미하며, 필드 값의 일치 여부는 출력으로 확인합니다.

테스트 보고서는 `object-mapper-api/build/reports/tests/test/index.html`에 생성됩니다. 이전 결과가 `UP-TO-DATE`여서 테스트를 다시 실행하려면 테스트 명령에 `--rerun-tasks`를 추가합니다.

## 애플리케이션 실행

테스트 외에 기본 웹 애플리케이션을 실행할 수도 있습니다. 아래 명령은 `ch2-spring-boot/`에서 실행합니다.

```bash
./gradlew :object-mapper-api:bootRun
```

기본 주소는 `http://localhost:8085`이며, 현재 등록된 API가 없어 기본 경로는 `404`를 반환합니다. 서버를 시작하는 것만으로 변환 테스트가 실행되지는 않습니다. 서버 종료는 `Ctrl+C`입니다.

빌드한 JAR로 실행하는 명령:

```bash
java -jar object-mapper-api/build/libs/object-mapper-api-0.0.1-SNAPSHOT.jar
```

IntelliJ IDEA에서는 ch2 Gradle 프로젝트를 새로고침한 뒤, 테스트 클래스 또는 `contextLoads()` 옆 실행 버튼으로 변환 예제를 실행합니다. 서버 실행은 `ObjectMapperApplication.main()`을 사용합니다.
