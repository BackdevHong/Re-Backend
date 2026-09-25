# Chapter 3. ObjectMapper — 중첩 객체와 JSON 트리

[전체 챕터 목차](../README.md) · [Chapter 2 ObjectMapper 기초](../ch2-spring-boot/object-mapper-api/README.md)

사용자와 자동차 목록을 JSON으로 직렬화하고, JSON 트리를 조회·수정하는 학습 예제입니다.
스프링 서버 없이 `Main.main()`에서 Jackson의 `ObjectMapper`를 직접 생성해 실행합니다.

## 개발 환경

- Java 실행 확인 환경: JDK 26
- Gradle Wrapper 9.3.0, `java` 플러그인
- Jackson Databind 2.15.0 (`com.fasterxml.jackson.databind.ObjectMapper`)
- JUnit Jupiter 6.0.0 의존성은 등록되어 있으나 현재 테스트 소스는 없습니다.

빌드 파일에 Java Toolchain이나 소스 버전이 지정되어 있지 않으므로 IDE와 Gradle의 JDK 설정을 확인하세요. 현재 진입점은 `static void main()` 형태입니다.

Chapter 2의 `object-mapper-api`는 Spring Boot 환경에서 `tools.jackson.databind.ObjectMapper`를 사용합니다. 이 프로젝트는 Jackson 2를 명시적으로 의존하는 독립 Gradle 프로젝트이므로 패키지와 실행 방식이 다릅니다.

## 프로젝트 구조

```text
ch3-object-mapper/
├── README.md
├── build.gradle
├── settings.gradle
├── gradlew
├── gradle/wrapper/
├── sample.json              # JSON 구조 참고 파일
└── src/main/java/
    ├── Main.java            # 객체 생성, 직렬화, 트리 조회와 수정
    └── dto/
        ├── User.java        # 이름, 나이, 자동차 목록
        └── Car.java         # 자동차 이름, 번호, 종류
```

## 데이터 구조와 필드 매핑

| 클래스 | Java 필드 | 타입 | JSON 필드 |
| --- | --- | --- | --- |
| `User` | `name` | `String` | `name` |
| `User` | `age` | `int` | `age` |
| `User` | `cars` | `List<Car>` | `cars` |
| `Car` | `name` | `String` | `name` |
| `Car` | `carNumber` | `String` | `car_number` |
| `Car` | `type` | `String` | `TYPE` |

`Car`의 `@JsonProperty`로 Java 필드와 JSON 속성 이름을 연결합니다. 이 설정은 직렬화뿐 아니라 JSON 배열을 다시 `Car` 목록으로 변환할 때도 적용됩니다.
두 DTO는 getter와 setter를 제공하며, 생성자를 직접 선언하지 않아 기본 생성자가 자동으로 제공됩니다.

## 학습 흐름

### 1. 중첩 객체를 JSON 문자열로 직렬화

`Main`은 이름이 `홍길동`, 나이가 `10`인 사용자를 만들고 K5와 Q5를 `List<Car>`에 담습니다.

```java
String json = objectMapper.writeValueAsString(user);
```

사용자 안의 자동차 목록까지 함께 변환됩니다.

```json
{
  "name": "홍길동",
  "age": 10,
  "cars": [
    {"name": "K5", "car_number": "11가 1234", "TYPE": "sedan"},
    {"name": "Q5", "car_number": "12가 1234", "TYPE": "SUV"}
  ]
}
```

실제 첫 출력은 줄바꿈 없는 JSON 문자열입니다.

### 2. JsonNode로 필요한 값 조회

```java
JsonNode jsonNode = objectMapper.readTree(json);
String name = jsonNode.get("name").asText();
int age = jsonNode.get("age").asInt();
```

`readTree()`는 JSON을 트리로 읽습니다. 전체를 DTO로 변환하지 않고도 속성 이름으로 노드를 선택하고 값을 꺼낼 수 있습니다.
현재 입력에는 `name`, `age`, `cars`가 항상 존재합니다. 다른 입력을 처리할 때는 누락된 필드와 노드 타입을 확인해야 합니다.

### 3. JSON 배열을 List<Car>로 변환

```java
ArrayNode arrayNode = (ArrayNode) jsonNode.get("cars");
List<Car> cars = objectMapper.convertValue(
        arrayNode, new TypeReference<List<Car>>() {});
```

`ArrayNode`는 JSON 배열을 나타냅니다. `TypeReference<List<Car>>`로 목록의 원소 타입까지 전달하면 Jackson이 각 원소를 `Car`로 변환합니다.
`List.class`만 전달하면 원소가 `Car`라는 제네릭 타입 정보를 전달할 수 없습니다.

출력:

```text
[Car{name='K5', carNumber='11가 1234', type='sedan'}, Car{name='Q5', carNumber='12가 1234', type='SUV'}]
```

### 4. ObjectNode로 JSON 값 수정

```java
ObjectNode objectNode = (ObjectNode) jsonNode;
objectNode.put("name", "steve");
objectNode.put("age", "20");
System.out.println(objectNode.toPrettyString());
```

`ObjectNode`는 JSON 객체의 속성을 수정할 수 있는 노드입니다. 위 코드는 같은 트리의 이름과 나이를 변경하고, 들여쓰기한 JSON을 출력합니다.
원래의 `User` 객체나 이미 생성된 `json` 문자열이 함께 변경되는 것은 아닙니다.

현재 `put("age", "20")`는 문자열을 전달하므로 최종 JSON의 나이도 **문자열 `"20"`**입니다. 숫자 타입을 유지하려면 `put("age", 20)`을 사용합니다.

```json
{
  "name": "steve",
  "age": "20",
  "cars": [
    {"name": "K5", "car_number": "11가 1234", "TYPE": "sedan"},
    {"name": "Q5", "car_number": "12가 1234", "TYPE": "SUV"}
  ]
}
```

## sample.json의 역할

`sample.json`은 중첩 JSON 구조와 `car_number`, `TYPE` 속성 이름을 확인하는 참고 파일입니다.
파일에는 이름이 `HongGilDong`, 자동차의 `TYPE`이 빈 문자열로 적혀 있어 실행 코드에서 생성하는 값과 다릅니다.
현재 `Main`은 이 파일을 읽거나 덮어쓰지 않고, 코드에서 만든 `User`를 입력으로 사용합니다.

## 빌드와 실행

저장소 루트에서 빌드합니다.

```bash
cd ch3-object-mapper
./gradlew build
```

현재는 `java` 플러그인만 사용하므로 `run` 또는 `bootRun` 태스크가 없습니다. 생성되는 JAR도 의존성을 포함한 실행용 JAR이 아닙니다.

IntelliJ IDEA에서 실행하는 방법:

1. `ch3-object-mapper/build.gradle`을 Gradle 프로젝트로 연결하고 의존성을 동기화합니다.
2. 프로젝트 SDK와 Gradle JVM을 확인합니다. 문서 검증에는 JDK 26 환경을 사용했습니다.
3. `src/main/java/Main.java`의 `main()` 옆 실행 버튼을 누릅니다.

콘솔에는 다음 순서로 출력됩니다.

1. 사용자와 자동차 두 대를 포함한 JSON 문자열
2. 트리에서 꺼낸 `홍길동`과 `10`
3. JSON 배열에서 변환한 자동차 목록
4. 이름이 `steve`, 나이가 문자열 `"20"`으로 변경된 JSON

## 검증 범위

문서 정리 시 Gradle 빌드와 `Main` 실행을 확인했습니다. 출력값을 비교해 중첩 직렬화, 트리 필드 조회, 자동차 목록 변환, 수정된 이름과 나이의 문자열 타입을 검증했고, `sample.json`의 JSON 문법도 확인했습니다.

현재 저장소에 자동 테스트는 없으므로 빌드에서 `test`는 `NO-SOURCE`로 표시됩니다. 빌드 성공이 테스트 케이스 통과를 의미하지는 않습니다.
