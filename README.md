# Re-Backend

Java로 백엔드 개발의 기초 개념을 학습하고 예제를 정리하는 저장소입니다.
각 챕터의 README에서 학습 내용, 코드 구조, 실행 방법을 확인할 수 있습니다.

## 챕터 목차

| 챕터 | 주제 | 학습 내용 |
| --- | --- | --- |
| [Chapter 1](ch1-design-pattern/README.md) | 디자인 패턴 (완료) | 싱글톤(Singleton), 어댑터(Adapter), 프록시(Proxy)와 AOP 개념 예제, 데코레이터(Decorator), 옵저버(Observer), 파사드(Facade), 전략(Strategy) |
| [Chapter 2](ch2-spring-boot/README.md) | 스프링 부트 (학습 중) | Gradle 멀티 모듈 구성, REST 컨트롤러, GET 요청 매핑, 경로 변수, 쿼리 파라미터와 DTO 바인딩, POST 요청 본문과 JSON 필드 매핑 |

## 개발 환경

| 챕터 | Java | 빌드 및 실행 |
| --- | --- | --- |
| Chapter 1 | JDK 26 | IntelliJ IDEA 또는 `javac`, `java` |
| Chapter 2 | JDK 25 (Gradle Toolchain) | Spring Boot 4.1.1, Gradle Wrapper 9.7.1 |

각 챕터는 독립적으로 실행합니다. 챕터별 의존성과 자세한 실행 방법은 각 챕터의 README를 참고하세요.

## 저장소 구조

```text
Re-Backend/
├── README.md                 # 전체 소개 및 챕터 목차
├── ch1-design-pattern/
│   ├── README.md             # Chapter 1 학습 내용 및 실행 방법
│   └── src/                  # 디자인 패턴 예제
└── ch2-spring-boot/
    ├── README.md             # Chapter 2 학습 내용 및 실행 방법
    ├── build.gradle          # 플러그인 버전과 모듈 공통 설정
    ├── settings.gradle       # 하위 모듈 등록
    ├── gradlew               # Gradle Wrapper 실행 스크립트
    ├── get-api/              # GET API 학습 모듈
    │   ├── README.md
    │   ├── build.gradle
    │   └── src/              # 애플리케이션 및 테스트
    ├── post-api/             # POST API 학습 모듈
    │   ├── README.md
    │   ├── build.gradle
    │   └── src/main/         # POST 컨트롤러, DTO, 실행 설정
    └── put-api/              # PUT API 기본 모듈
        ├── README.md
        ├── build.gradle
        └── src/main/         # 실행 클래스와 설정
```
