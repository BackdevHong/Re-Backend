# ObjectMapper API 모듈

[Chapter 2 모듈 목차](../README.md)

ObjectMapper를 이용한 JSON과 자바 객체 변환을 학습하기 위한 기본 Spring Boot 모듈입니다. 현재는 실행 클래스와 설정만 있으며 컨트롤러, DTO, 변환 예제는 추가하지 않았습니다.

- 실행 클래스: `org.honginsung.objectmapper.ObjectMapperApplication`
- 기본 포트: `8085` (기존 모듈의 8080~8084와 함께 실행 가능)
- 공통 환경: JDK 25, Spring Boot 4.1.1, Gradle Wrapper 9.7.1
- 의존성: Spring MVC 및 테스트용 스타터

## 실행 및 빌드

저장소 루트에서 다음 명령을 실행합니다.

```bash
cd ch2-spring-boot
./gradlew :object-mapper-api:build
./gradlew :object-mapper-api:bootRun
```

서버는 `http://localhost:8085`에서 시작합니다. 등록된 API가 없으므로 기본 경로 요청에는 `404`가 반환됩니다. 종료는 실행 터미널에서 `Ctrl+C`를 누릅니다.

빌드한 JAR로도 실행할 수 있습니다.

```bash
java -jar object-mapper-api/build/libs/object-mapper-api-0.0.1-SNAPSHOT.jar
```

현재 테스트 소스는 없습니다. IntelliJ IDEA에서 ch2 Gradle 프로젝트를 새로고침하면 `object-mapper-api` 모듈이 나타나며, `ObjectMapperApplication.main()`으로 실행할 수 있습니다.
