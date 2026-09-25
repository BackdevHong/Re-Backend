# Chapter 3. IoC — 스프링 컨테이너

[전체 챕터 목차](../README.md) · [순수 Java DI 예제](../ch3-di/README.md)

DI 예제에서 직접 만들던 인코더를 스프링 빈으로 등록하고, 컨테이너가 의존 객체를 연결하도록 구성한 챕터입니다.
현재 실행 예제는 `base64Encode` 빈을 조회해 문자열의 Base64 인코딩 결과를 출력합니다.

## 개발 환경

- Java 25 (Gradle Toolchain)
- Spring Boot 4.1.1 / Gradle Wrapper 9.7.1
- `spring-boot-starter-webmvc`, JUnit 기반 Spring Boot 테스트
- Chapter 2의 멀티 모듈 빌드와 분리된 독립 Gradle 프로젝트입니다.

## 프로젝트 구조

```text
ch3-ioc/
├── README.md
├── build.gradle
├── settings.gradle
├── gradlew
├── gradle/wrapper/
└── src/
    ├── main/
    │   ├── java/org/honginsung/ioc/
    │   │   ├── IocApplication.java             # 실행 진입점과 AppConfig
    │   │   ├── ApplicationContextProvider.java # 컨텍스트 보관 및 조회
    │   │   ├── IEncoder.java                   # 인코딩 인터페이스
    │   │   ├── Encoder.java                    # 주입받은 인코더에 위임
    │   │   ├── Base64Encoder.java              # Base64 인코딩 컴포넌트
    │   │   └── UrlEncoder.java                 # URL 인코딩 컴포넌트
    │   └── resources/application.properties
    └── test/java/org/honginsung/ioc/
        └── IocApplicationTests.java            # 컨텍스트 로딩 테스트
```

## 학습 내용

### 1. IoC와 DI의 관계

IoC(Inversion of Control)는 객체 생성과 연결 등의 제어를 외부로 옮기는 개념이며, DI(Dependency Injection)는 사용할 의존 객체를 외부에서 전달하는 방식입니다.

| 구분 | `ch3-di` | `ch3-ioc` |
| --- | --- | --- |
| 구현체 생성 | `Main`에서 직접 `new` | 스프링이 `@Component` 클래스를 빈으로 생성 |
| 객체 연결 | `new Encoder(new UrlEncoder())` | 스프링이 `@Bean` 메서드의 인자를 공급하고, 메서드에서 `Encoder` 생성 |
| 객체 사용 | 직접 만든 객체 사용 | `ApplicationContext`에서 등록된 빈 조회 |

`Encoder`는 두 예제 모두 `IEncoder`에 의존합니다. 어떤 구현체를 사용할지는 외부의 객체 구성 코드에서 결정합니다.

### 2. 컴포넌트 스캔과 빈 등록

`@SpringBootApplication`이 있는 패키지와 하위 패키지를 대상으로 컴포넌트 스캔이 이루어집니다.
`Base64Encoder`, `UrlEncoder`, `ApplicationContextProvider`에는 `@Component`가 붙어 있어 스프링이 관리하는 빈으로 등록됩니다.

`IocApplication.java` 안의 `AppConfig`는 `@Configuration` 설정 클래스입니다. `@Bean` 메서드에서 구성한 `Encoder` 객체 두 개를 등록합니다.

| 빈 이름 | 타입 | 연결된 인코더 | 등록 방식 |
| --- | --- | --- | --- |
| `base64Encoder` | `Base64Encoder` | 해당 없음 | `@Component` |
| `urlEncoder` | `UrlEncoder` | 해당 없음 | `@Component` |
| `base64Encode` | `Encoder` | `Base64Encoder` | `@Bean("base64Encode")` |
| `urlEncode` | `Encoder` | `UrlEncoder` | `@Bean("urlEncode")` |

스프링은 `@Bean` 메서드의 매개변수 타입에 맞는 빈을 전달합니다. 각 메서드는 전달받은 인코더를 `Encoder` 생성자에 주입하고, 반환한 객체를 컨테이너에 등록합니다.
`Encoder` 자체에는 `@Component`가 붙어 있지 않습니다.

### 3. 컨텍스트와 빈 조회

`ApplicationContextProvider`는 `ApplicationContextAware`를 구현합니다. 스프링이 호출하는 `setApplicationContext()`에서 컨텍스트를 정적 필드에 보관하고, `getContext()`로 제공합니다.
실행 진입점은 `SpringApplication.run()`이 끝난 뒤 이 컨텍스트에서 빈을 조회합니다.

```java
ApplicationContext context = ApplicationContextProvider.getContext();
Encoder encoder = context.getBean("base64Encode", Encoder.class);
String result = encoder.encode(url);
```

`Encoder` 타입의 빈이 두 개 있으므로 이름과 타입을 함께 지정해 사용할 객체를 선택합니다. URL 인코딩 결과를 보려면 조회 이름을 `"urlEncode"`로 바꾸면 됩니다.

`Encoder`에는 `setIEncoder()`도 있어 의존 객체를 나중에 교체할 수 있지만, 현재 실행 경로는 생성자 주입을 사용합니다. setter를 선언하는 것만으로 자동 주입이 이루어지는 것은 아닙니다.

### 4. 학습 중 확인한 오류

- **조회 타입 불일치**: `context.getBean(UrlEncoder.class)`의 결과는 `UrlEncoder` 또는 공통 인터페이스인 `IEncoder`로 받을 수 있습니다. 별도 구현체인 `Base64Encoder` 변수에는 대입할 수 없습니다.
- **중복된 `@Bean` 메서드 이름**: 현재 `@Configuration` 기본 설정에서는 매개변수 타입이 달라도 같은 이름의 `@Bean` 메서드를 허용하지 않습니다. 빈 이름만 다르게 지정해도 이 검사는 적용됩니다. 최종 코드에서는 메서드 이름도 `base64Encode`, `urlEncode`로 구분했습니다.

## 실행 방법

저장소 루트에서 다음 명령을 실행합니다.

```bash
cd ch3-ioc
./gradlew bootRun
```

입력 문자열:

```text
www.naver.com/books/it?page=10&page=20&name=spring-boot
```

기본 Base64 출력:

```text
d3d3Lm5hdmVyLmNvbS9ib29rcy9pdD9wYWdlPTEwJnBhZ2U9MjAmbmFtZT1zcHJpbmctYm9vdA==
```

`urlEncode` 빈을 선택했을 때의 출력:

```text
www.naver.com%2Fbooks%2Fit%3Fpage%3D10%26page%3D20%26name%3Dspring-boot
```

현재 HTTP 컨트롤러는 없지만 웹 의존성이 포함되어 기본 포트 `8080`으로 서버가 실행됩니다. 인코딩 결과 출력 후에도 프로세스는 유지되며, `Ctrl+C`로 종료합니다. 포트가 사용 중이면 다음처럼 변경할 수 있습니다.

```bash
./gradlew bootRun --args='--server.port=8086'
```

IntelliJ IDEA에서는 `ch3-ioc/build.gradle`을 Gradle 프로젝트로 연결한 뒤 `IocApplication.main()`을 실행합니다.

## 빌드와 테스트

`ch3-ioc/`에서 실행합니다.

```bash
./gradlew build
./gradlew test
```

`IocApplicationTests.contextLoads()`는 `@SpringBootTest`로 컨텍스트 초기화와 빈 구성이 성공하는지 확인합니다. 현재 테스트에는 인코딩 결과에 대한 단언문이 없으며, `main()`의 콘솔 출력도 별도로 검증하지 않습니다.
