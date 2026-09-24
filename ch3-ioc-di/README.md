# Chapter 3. IoC와 DI

[전체 챕터 목차](../README.md)

인코더 예제로 IoC(제어의 역전)와 DI(의존성 주입)의 기본 개념을 학습합니다.
현재는 순수 Java에서 인터페이스와 생성자 주입을 사용해 객체를 연결하며, 스프링 컨테이너는 사용하지 않습니다.

## 개발 환경

- **JDK 26**: 저장소의 IntelliJ IDEA 프로젝트 SDK를 상속합니다.
- 외부 라이브러리 없이 Java 표준 라이브러리로 실행합니다.
- Chapter 2의 Gradle 멀티 모듈과 별개인 독립 Java 모듈입니다.

## 챕터 구조

```text
ch3-ioc-di/
├── README.md
├── ch3-ioc-di.iml
└── src/org/honginsung/di/
    ├── Main.java           # 구현체 생성과 주입, 예제 실행
    ├── IEncoder.java       # 문자열 인코딩 인터페이스
    ├── Encoder.java        # 주입받은 인코더로 처리를 위임
    ├── UrlEncoder.java     # UTF-8 URL 인코딩
    └── Base64Encoder.java  # Base64 인코딩
```

## 학습 내용

### 1. 인터페이스로 인코딩 방식 분리

`IEncoder`는 `encode(String message)`를 정의합니다. `UrlEncoder`와 `Base64Encoder`는 이 인터페이스를 구현해 서로 다른 인코딩 방식을 제공합니다.

| 클래스 | 처리 방식 |
| --- | --- |
| `UrlEncoder` | `URLEncoder.encode(message, StandardCharsets.UTF_8)`로 변환 |
| `Base64Encoder` | 문자열의 바이트를 `Base64.getEncoder()`로 변환 |

`Encoder`는 구체적인 인코딩 방식 대신 `IEncoder`에 의존하고, `encode()`에서 주입받은 객체에 처리를 위임합니다.

### 2. 생성자를 통한 DI

DI(Dependency Injection)는 객체가 사용할 의존 객체를 외부에서 전달하는 방식입니다.

현재 `Main`은 `new UrlEncoder()`로 의존 객체를 생성해 `new Encoder(...)`의 생성자로 전달합니다.
`Encoder`는 전달받은 `IEncoder`를 `final` 필드에 보관하므로 생성 시 정한 인코더를 사용합니다.

```java
Encoder encoder = new Encoder(new UrlEncoder());
String result = encoder.encode(url);
```

Base64 인코딩을 사용하려면 `Main`의 생성 부분에서 `new UrlEncoder()`를 `new Base64Encoder()`로 바꾸면 됩니다. 이때 `Encoder`의 구현을 수정할 필요가 없습니다.

### 3. IoC와 객체 구성 책임

IoC(Inversion of Control)는 객체 생성이나 실행 흐름에 대한 제어를 외부로 옮기는 개념입니다.
이 예제에서 어떤 인코더를 만들고 연결할지는 `Main`이 결정하고, `Encoder`는 전달받은 객체를 사용해 인코딩하는 역할만 맡습니다.

DI는 IoC를 구현하는 방법 중 하나입니다. 현재 코드는 `Main`에서 직접 객체를 조립하는 생성자 주입 예제이며, 스프링의 빈 등록·자동 주입은 아직 포함하지 않습니다.

## 실행 방법

### 터미널

저장소 루트(`Re-Backend/`)에서 실행합니다. 아래 명령은 macOS·Linux 셸 기준입니다.

```bash
java -version
javac -version

mkdir -p out/ioc-di
javac -encoding UTF-8 -d out/ioc-di ch3-ioc-di/src/org/honginsung/di/*.java
java -cp out/ioc-di org.honginsung.di.Main
```

현재 `Main`은 다음 문자열을 `UrlEncoder`에 전달합니다.

```text
www.naver.com/books/it?page=10&page=20&name=spring-boot
```

출력:

```text
www.naver.com%2Fbooks%2Fit%3Fpage%3D10%26page%3D20%26name%3Dspring-boot
```

`URLEncoder`는 폼 데이터 인코딩 방식으로 입력 문자열 전체를 변환하므로 `/`, `?`, `=`, `&`도 인코딩됩니다. 이 예제는 문자열 변환만 수행하며 웹 요청을 보내지 않습니다.

컴파일 결과는 Git에서 제외되는 `out/ioc-di/`에 생성됩니다. 현재 별도 자동 테스트는 없으며 `Main` 실행 결과로 동작을 확인합니다.

### IntelliJ IDEA

1. 저장소 루트 프로젝트를 열고 프로젝트 SDK를 JDK 26으로 설정합니다.
2. `ch3-ioc-di` 모듈의 `src`가 소스 루트로 인식되는지 확인합니다.
3. `org.honginsung.di.Main`의 `main()` 옆 실행 버튼을 누릅니다.

현재 진입점은 `static void main()` 형태입니다. IDE와 터미널 모두 위 JDK 환경을 사용하세요.
