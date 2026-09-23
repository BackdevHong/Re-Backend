# Chapter 1. 디자인 패턴

[전체 챕터 목차](../README.md)

Java 예제로 싱글톤, 어댑터, 프록시 패턴의 구조와 동작을 학습합니다.
프록시를 활용해 캐싱과 실행 전후 부가 기능을 적용하는 AOP 개념도 살펴봅니다.

## 개발 환경

- **Java / JDK 26**: 현재 IntelliJ IDEA 프로젝트에 설정된 버전입니다.
- **IntelliJ IDEA**: IDE에서 프로젝트를 열어 실행할 수 있습니다.
- 외부 라이브러리나 Maven·Gradle 설정 없이 Java 소스만으로 실행합니다.

## 챕터 구조

```text
ch1-design-pattern/
├── README.md
└── src/
    ├── SingletonMain.java       # 싱글톤 패턴 실행 예제
    ├── AdapterMain.java         # 어댑터 패턴 실행 예제
    ├── ProxyMain.java           # 프록시 및 실행 시간 측정 예제
    ├── singleton/
    │   ├── SocketClient.java    # 공유 인스턴스 생성 및 반환
    │   ├── AClazz.java          # 공유 인스턴스를 사용하는 클래스
    │   └── BClazz.java          # 공유 인스턴스를 사용하는 클래스
    ├── adapter/
    │   ├── Electronic110V.java  # 110V 기기 인터페이스
    │   ├── Electronic220V.java  # 220V 기기 인터페이스
    │   ├── HairDryer.java       # 110V 기기 구현
    │   ├── Cleaner.java         # 220V 기기 구현
    │   ├── AirConditioner.java  # 220V 기기 구현
    │   └── SocketAdapter.java   # 두 인터페이스를 연결하는 어댑터
    └── proxy/
        ├── IBrowser.java        # 브라우저 공통 인터페이스
        ├── Html.java            # URL을 보관하는 HTML 예제 객체
        ├── Browser.java         # 호출마다 HTML 객체 생성
        ├── BrowserProxy.java    # HTML 객체를 캐싱하는 프록시
        └── aop/
            └── AopBrowser.java # 실행 전후 콜백과 캐싱 적용
```

## 학습 내용

### 1. 싱글톤 패턴 (Singleton)

하나의 인스턴스를 생성하고 여러 곳에서 공유하는 패턴입니다.

- `SocketClient`의 생성자를 `private`으로 선언해 외부에서 직접 생성하지 못하도록 합니다.
- `getInstance()`를 처음 호출할 때 인스턴스를 생성하고, 이후에는 기존 인스턴스를 반환합니다.
- `AClazz`와 `BClazz`가 가져온 `SocketClient`를 비교해 같은 인스턴스를 공유하는지 확인합니다.

현재 예제는 지연 초기화의 기본 구조를 보여주며, 동시 호출에 대한 동기화는 구현하지 않았습니다.

### 2. 어댑터 패턴 (Adapter)

서로 다른 인터페이스를 가진 객체를 함께 사용할 수 있도록 중간에서 연결하는 패턴입니다.

- `AdapterMain.connect()`는 `Electronic110V` 타입을 받습니다.
- `HairDryer`는 `Electronic110V`를 구현하므로 바로 연결할 수 있습니다.
- `Cleaner`와 `AirConditioner`는 `Electronic220V`를 구현하므로 `SocketAdapter`로 감싸서 연결합니다.
- `SocketAdapter`는 `powerOn()` 호출을 내부 220V 기기의 `connect()` 호출로 위임합니다.

전압과 전자기기는 인터페이스 간 연결을 설명하기 위한 비유입니다.

### 3. 프록시 패턴 (Proxy)

대상과 같은 인터페이스를 제공하는 대리 객체를 통해 접근을 제어하거나 캐싱 같은 부가 기능을 적용하는 패턴입니다.

- `IBrowser`는 `Html`을 반환하는 `show()`를 정의합니다.
- `Browser`는 `show()`를 호출할 때마다 새로운 `Html` 객체를 생성합니다.
- `BrowserProxy`는 첫 호출에서 `Html`을 생성해 필드에 저장하고, 이후 호출에서는 같은 객체를 반환합니다.
- 이 예제의 `BrowserProxy`는 내부에 `Browser`를 두고 위임하는 대신, 직접 `Html` 생성과 재사용을 구현해 캐싱 동작을 보여줍니다.

`Html`은 URL을 보관하는 단순 객체입니다. 예제는 실제 웹 요청이나 HTML 다운로드를 수행하지 않습니다.

#### AOP 개념: 실행 전후 부가 기능

AOP(관점 지향 프로그래밍)는 시간 측정이나 로깅 같은 공통 관심사를 핵심 동작과 분리하는 방식입니다. `AopBrowser`는 프레임워크 없이 `Runnable` 콜백으로 실행 전후 동작을 전달받아 이 개념을 보여줍니다.

1. `show()`에서 `before.run()`을 호출합니다. `ProxyMain`의 콜백은 `before`를 출력하고 시작 시간을 저장합니다.
2. 캐시가 비어 있으면 `Html`을 생성하고, `Thread.sleep(1500)`으로 로딩 시간을 흉내 냅니다.
3. `after.run()`에서 경과 시간을 계산하고, 캐시 사용 메시지를 출력한 뒤 `Html`을 반환합니다.

`ProxyMain`은 같은 `AopBrowser`에서 `show()`를 두 번 호출합니다. 첫 호출에는 약 1.5초가 걸리고, 두 번째 호출은 캐시를 사용해 빠르게 끝납니다. 측정값은 밀리초 단위이며 실행 환경에 따라 달라집니다. 두 콜백은 매 호출마다 실행되며, `after` 콜백은 캐시 사용 메시지 출력 전에 호출됩니다.

`Browser`와 `BrowserProxy`의 반복 호출 예제도 `ProxyMain`에 주석으로 남아 있습니다. 직접 비교하려면 해당 블록의 주석을 해제하고 사용하는 클래스의 import를 추가하면 됩니다.

## 실행 방법

### 터미널

JDK 26이 설치되어 있고 `java`, `javac` 명령을 사용할 수 있는지 확인합니다.

```bash
java -version
javac -version
```

저장소 루트(`Re-Backend/`)에서 아래 명령으로 이 챕터의 예제를 컴파일합니다. 명령은 macOS·Linux 셸 기준입니다.

```bash
mkdir -p out/design-pattern
javac -encoding UTF-8 -d out/design-pattern \
  ch1-design-pattern/src/*.java \
  ch1-design-pattern/src/singleton/*.java \
  ch1-design-pattern/src/adapter/*.java \
  ch1-design-pattern/src/proxy/*.java \
  ch1-design-pattern/src/proxy/aop/*.java
```

싱글톤 예제:

```bash
java -cp out/design-pattern SingletonMain
```

```text
두개의 객체가 동일한가?
true
```

어댑터 예제:

```bash
java -cp out/design-pattern AdapterMain
```

```text
헤어 드라이기 110v on
청소기 220v on
에어컨 220v on
```

프록시 예제(현재 기본 실행은 `AopBrowser`):

```bash
java -cp out/design-pattern ProxyMain
```

출력 예시이며, 숫자는 실제 실행 시간에 따라 달라집니다.

```text
before
AopBrowser html loading from : www.google.com
AopBrowser html use cache : www.google.com
1500
before
AopBrowser html use cache : www.google.com
0
```

첫 호출에서도 HTML을 저장한 뒤 캐시 사용 메시지를 출력합니다. 두 번째 호출에서는 로딩 메시지와 대기 과정이 생략됩니다.

컴파일 결과는 Git에서 제외되는 `out/` 디렉터리에 생성됩니다.

### IntelliJ IDEA

1. 저장소 루트 폴더를 프로젝트로 엽니다.
2. **File → Project Structure → Project SDK**를 JDK 26으로 설정합니다.
3. `ch1-design-pattern/src`가 소스 루트로 인식되는지 확인합니다. 인식되지 않으면 해당 폴더에서 **Mark Directory as → Sources Root**를 선택합니다.
4. `SingletonMain`, `AdapterMain`, `ProxyMain` 중 실행할 클래스의 `main()` 옆 실행 버튼을 누릅니다.

세 예제는 `static void main()` 형태의 진입점을 사용합니다. 실행 문제가 발생하면 IDE의 프로젝트 SDK와 터미널의 JDK 버전이 위 환경과 일치하는지 확인합니다.
