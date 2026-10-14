# Chapter 4. 스프링 부트 심화

[전체 챕터 목차](../README.md)

Chapter 2와 같은 방식으로 학습 주제별 애플리케이션을 관리하는 Gradle 멀티 모듈 프로젝트입니다.
루트는 공통 설정과 모듈 등록을 담당하고, 각 서브 프로젝트는 자체 소스·의존성·설정·테스트를 갖습니다.

## 모듈 목차

| 모듈 | 내용 | 실행 클래스 |
| --- | --- | --- |
| [validation](validation/README.md) | 요청 DTO 검증, 중첩 객체 검증, BindingResult 오류 응답, 사용자 정의 제약 조건 | `org.honginsung.validation.ValidationApplication` |

현재는 `validation` 모듈이 등록되어 있으며, 이후 학습 주제에 맞춰 같은 위치에 모듈을 추가합니다. `POST /api/user`의 요청·응답 예제와 검증 범위는 [Validation README](validation/README.md)에 정리했습니다.

## 개발 환경

- Java 25 (공통 Gradle Toolchain)
- Spring Boot 4.1.1
- Gradle Wrapper 9.7.1
- 의존성 관리 플러그인 1.1.7

## 프로젝트 구조

```text
ch4-more-springboot/
├── README.md
├── settings.gradle           # 하위 모듈 등록
├── build.gradle              # 플러그인 버전, Java·저장소·테스트 공통 설정
├── gradlew / gradlew.bat
├── gradle/wrapper/
└── validation/
    ├── README.md             # 검증 규칙과 API 호출 예제
    ├── build.gradle          # Spring Boot 플러그인과 모듈 의존성
    └── src/
        ├── main/java/        # 컨트롤러, DTO, 사용자 정의 검증기
        ├── main/resources/   # application.properties
        └── test/java/        # 컨텍스트 로딩 테스트
```

루트에는 실행 소스를 두지 않습니다. Spring Boot 플러그인은 루트에서 버전만 선언하고 각 애플리케이션 모듈에서 적용합니다.
JDK 25, UTF-8, `-parameters`, Maven Central, JUnit Platform은 모든 하위 모듈에 공통 적용됩니다.

## 빌드와 실행

저장소 루트에서 아래 명령을 실행합니다. Windows에서는 `./gradlew` 대신 `gradlew.bat`을 사용합니다.

```bash
cd ch4-more-springboot

# 모듈 목록
./gradlew projects

# 전체 모듈 빌드와 테스트
./gradlew build

# 특정 모듈 빌드와 테스트
./gradlew :validation:build
./gradlew :validation:test

# 특정 모듈 서버 실행
./gradlew :validation:bootRun
```

애플리케이션 이름은 `spring.application.name=validation`입니다. 기본 포트는 `8080`이며, 서버는 `Ctrl+C`로 종료합니다. 다른 애플리케이션과 동시에 실행할 때는 포트를 구분합니다.

```bash
./gradlew :validation:bootRun --args='--server.port=8088'
```

빌드한 애플리케이션 JAR도 독립적으로 실행할 수 있습니다.

```bash
java -jar validation/build/libs/validation-0.0.1-SNAPSHOT.jar
```

테스트 보고서는 `validation/build/reports/tests/test/index.html`에 생성됩니다. 현재 테스트는 스프링 컨텍스트 초기화를 확인합니다.

## 새 서브 프로젝트 추가 방법

아래 `new-api`는 추가 절차를 설명하기 위한 이름입니다.

1. `ch4-more-springboot/new-api/` 아래에 `build.gradle`과 `src/main/java`, `src/main/resources`, 필요하면 `src/test/java`를 만듭니다.
2. 루트 `settings.gradle`에 `include 'new-api'`를 추가합니다.
3. 모듈의 `build.gradle`에 필요한 의존성을 선언합니다. 기본 웹 애플리케이션은 다음 구성을 사용할 수 있습니다.

```groovy
plugins {
    id 'org.springframework.boot'
}

dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-webmvc'
    testImplementation 'org.springframework.boot:spring-boot-starter-webmvc-test'
    testRuntimeOnly 'org.junit.platform:junit-platform-launcher'
}
```

4. 모듈의 기본 패키지에 `@SpringBootApplication` 실행 클래스를 만들고, `application.properties`에 애플리케이션 이름과 필요한 포트를 설정합니다.
5. `./gradlew :new-api:build`, `./gradlew :new-api:bootRun`으로 확인합니다.

Wrapper와 `settings.gradle`은 챕터 루트의 것을 공유하므로 하위 모듈마다 만들지 않습니다. 모듈 간 의존성도 자동으로 추가되지 않으며, 각 앱에 필요한 라이브러리만 모듈별로 선언합니다.

## IntelliJ IDEA

1. `ch4-more-springboot/settings.gradle` 또는 루트 `build.gradle`을 Gradle 프로젝트로 연결합니다.
2. Gradle JVM을 JDK 25로 설정하고 **Reload All Gradle Projects**를 실행합니다.
3. `validation` 하위 모듈이 표시되는지 확인합니다.
4. `ValidationApplication.main()` 또는 해당 모듈의 `bootRun`으로 실행합니다.

기존 실행 구성이 루트 소스 경로를 가리키면 이동된 클래스에서 다시 생성합니다.
