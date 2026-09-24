# Chapter 2. 스프링 부트

[전체 챕터 목차](../README.md)

스프링 학습 주제별 프로젝트를 독립적인 애플리케이션으로 실행하는 Gradle 멀티 모듈 프로젝트입니다.
GET 요청 예제는 `get-api`에, POST 요청 본문과 JSON 필드 매핑 예제는 `post-api`에 구성했습니다. PUT 요청의 중첩 DTO와 JSON 응답 예제는 `put-api`에, DELETE 학습용 기본 실행 구조는 `delete-api`에 구성했습니다. 이후 학습 프로젝트는 같은 위치에 새 모듈로 추가합니다.

## 모듈 목차

| 모듈 | 학습 내용 | 실행 클래스 |
| --- | --- | --- |
| [get-api](get-api/README.md) | REST 컨트롤러, GET 요청 매핑, 경로 변수, 쿼리 파라미터와 DTO 바인딩 | `org.honginsung.get.GetApplication` |
| [post-api](post-api/README.md) | POST 요청 본문, Map·DTO 바인딩, JSON 필드 이름 매핑 | `org.honginsung.post.PostApplication` |
| [put-api](put-api/README.md) | PUT 요청, 중첩 DTO, snake_case 변환, JSON 응답과 경로 변수 | `org.honginsung.put.PutApplication` |
| [delete-api](delete-api/README.md) | 기본 실행 구조 (API 예제 미포함) | `org.honginsung.delete.DeleteApplication` |

API 목록과 요청 예시는 [GET API 모듈 README](get-api/README.md), [POST API 모듈 README](post-api/README.md), [PUT API 모듈 README](put-api/README.md)에 정리했습니다.

## 개발 환경

| 항목 | 설정 |
| --- | --- |
| Java | JDK 25 (공통 Toolchain) |
| Spring Boot | 4.1.1 |
| Gradle | 9.7.1 (Wrapper 포함) |
| 의존성 관리 플러그인 | `io.spring.dependency-management` 1.1.7 |

JDK 25를 설치한 뒤 Gradle Wrapper로 실행합니다. 최초 실행에는 Gradle과 의존성을 다운로드할 인터넷 연결이 필요합니다.

## 프로젝트 구조

```text
ch2-spring-boot/
├── README.md
├── settings.gradle              # 모듈 등록
├── build.gradle                 # 플러그인 버전과 공통 Java·테스트 설정
├── gradlew / gradlew.bat         # 모든 모듈이 공유하는 Gradle Wrapper
├── gradle/wrapper/
├── docs/images/                 # 학습 스크린샷
├── get-api/
│   ├── README.md                # GET API 학습 내용과 실행 방법
│   ├── build.gradle             # 모듈별 플러그인과 의존성
│   └── src/
│       ├── main/                # 애플리케이션과 설정
│       └── test/                # 모듈 테스트
├── post-api/
│   ├── README.md                # POST API 학습 내용과 실행 방법
│   ├── build.gradle
│   └── src/main/                # POST 컨트롤러, DTO, 실행 설정
├── put-api/
│   ├── README.md                # PUT API 학습 내용과 실행 방법
│   ├── build.gradle
│   └── src/main/                # PUT 컨트롤러, 중첩 DTO, 실행 설정
└── delete-api/
    ├── README.md                # 기본 모듈 실행 방법
    ├── build.gradle
    └── src/main/                # 실행 클래스와 설정
```

루트 프로젝트는 모듈을 관리하며 실행 가능한 애플리케이션을 만들지 않습니다. 각 모듈은 자체 소스, 설정, 의존성과 빌드 결과를 가집니다.

- 루트 `settings.gradle`: `include 'get-api'`처럼 하위 프로젝트를 등록합니다.
- 루트 `build.gradle`: Spring Boot 플러그인 버전, 그룹·버전, JDK 25, Maven Central, UTF-8, `-parameters`, JUnit Platform을 공통으로 설정합니다.
- 모듈 `build.gradle`: Spring Boot 플러그인을 적용하고 그 모듈에 필요한 의존성을 선언합니다.

## 실행과 검증

아래 명령은 저장소 루트에서 시작합니다. Windows에서는 `./gradlew` 대신 `gradlew.bat`을 사용합니다.

```bash
cd ch2-spring-boot

# 등록된 모듈 확인
./gradlew projects

# 모든 모듈 테스트·빌드
./gradlew clean build

# 특정 모듈 테스트·빌드
./gradlew :get-api:test
./gradlew :get-api:build

# GET API 서버 실행 (8080)
./gradlew :get-api:bootRun

# POST API 서버 실행 (별도 터미널, 8081)
./gradlew :post-api:bootRun

# PUT API 서버 실행 (별도 터미널, 8082)
./gradlew :put-api:bootRun

# DELETE API 기본 서버 실행 (별도 터미널, 8083)
./gradlew :delete-api:bootRun
```

별도 터미널에서 API를 확인합니다.

```bash
curl -i http://localhost:8080/api/hello
```

정상 응답은 `200 OK`, 본문은 `hello spring boot`입니다. 서버 종료는 `Ctrl+C`입니다.

빌드한 JAR도 독립적으로 실행할 수 있습니다.

```bash
java -jar get-api/build/libs/get-api-0.0.1-SNAPSHOT.jar
```

테스트 보고서는 `get-api/build/reports/tests/test/index.html`에 생성됩니다. `get-api`는 기본 포트 8080, `post-api`는 8081, `put-api`는 8082, `delete-api`는 8083을 사용합니다. 실행 시 다른 포트로 변경할 수도 있습니다.

```bash
./gradlew :get-api:bootRun --args='--server.port=8084'
```

실행 명령에 `:모듈명:`을 명시하면 모듈이 늘어나도 원하는 애플리케이션을 선택할 수 있습니다.

## 새 학습 모듈 추가 방법

예를 들어 다음 학습 프로젝트를 `new-api`로 추가할 때는 아래 순서로 구성합니다. `new-api`는 추가 방법을 설명하기 위한 이름이며 현재 등록된 모듈은 `get-api`, `post-api`, `put-api`, `delete-api`입니다.

1. `ch2-spring-boot/new-api/`에 `build.gradle`, `src/main/java`, `src/main/resources`, `src/test/java`를 만듭니다.
2. 루트 `settings.gradle`에 `include 'new-api'`를 추가합니다.
3. 모듈의 `build.gradle`에 필요한 플러그인과 의존성을 선언합니다. 웹 애플리케이션의 기본 설정은 다음과 같습니다.

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

4. 모듈의 기본 패키지에 `@SpringBootApplication` 실행 클래스를 만들고 하위 패키지에 컨트롤러를 배치합니다.
5. `src/main/resources/application.properties`에 `spring.application.name=new-api`를 설정합니다. 다른 모듈과 동시에 실행한다면 `server.port=8084`처럼 별도 포트도 설정합니다.
6. `./gradlew :new-api:build`, `./gradlew :new-api:bootRun`으로 검증·실행하고 이 문서의 모듈 목차에 링크를 추가합니다.

새 모듈마다 Wrapper나 별도 `settings.gradle`을 만들 필요가 없습니다. 각 앱의 의존성은 해당 모듈에 선언하고, 공통으로 사용할 코드가 실제로 생기면 별도 라이브러리 모듈로 분리할 수 있습니다.

## IntelliJ IDEA

1. `ch2-spring-boot/settings.gradle` 또는 루트 `build.gradle`을 Gradle 프로젝트로 연결합니다.
2. Gradle JVM을 JDK 25로 설정하고 **Reload All Gradle Projects**를 실행합니다.
3. Gradle 창에 `get-api`, `post-api`, `put-api`, `delete-api` 하위 프로젝트가 나타나는지 확인합니다.
4. 실행할 모듈의 `GetApplication.main()`, `PostApplication.main()`, `PutApplication.main()`, `DeleteApplication.main()`을 실행합니다. Gradle 창에서 해당 모듈의 `Tasks → application → bootRun`으로도 실행할 수 있습니다.

기존 실행 구성이 이전 소스 경로를 참조하면 새 위치의 실행 클래스에서 다시 생성합니다. 빌드 결과와 IDE의 로컬 캐시는 Git에서 제외합니다.
