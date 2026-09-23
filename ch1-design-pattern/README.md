# Chapter 1. 디자인 패턴

[전체 챕터 목차](../README.md)

Java 예제로 싱글톤, 어댑터, 프록시, 데코레이터, 옵저버, 파사드 패턴의 구조와 동작을 학습합니다.
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
    ├── DecoratorMain.java       # 모델별 가격을 추가하는 데코레이터 예제
    ├── ObserverMain.java        # 버튼 클릭 이벤트를 전달하는 옵저버 예제
    ├── FacadeMain.java          # 연결과 파일 작업을 묶는 파사드 예제
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
    ├── proxy/
    │   ├── IBrowser.java        # 브라우저 공통 인터페이스
    │   ├── Html.java            # URL을 보관하는 HTML 예제 객체
    │   ├── Browser.java         # 호출마다 HTML 객체 생성
    │   ├── BrowserProxy.java    # HTML 객체를 캐싱하는 프록시
    │   └── aop/
    │       └── AopBrowser.java  # 실행 전후 콜백과 캐싱 적용
    ├── decorator/
    │   ├── ICar.java            # 가격 조회 및 출력 인터페이스
    │   ├── Audi.java            # 기본 가격을 가진 자동차
    │   ├── AudiDecorator.java  # 자동차를 감싸 모델별 가격 추가
    │   ├── A3.java              # 추가 가격 1000
    │   ├── A4.java              # 추가 가격 2000
    │   └── A5.java              # 추가 가격 3000
    ├── observer/
    │   ├── Button.java          # 클릭 이벤트를 발생시키는 객체
    │   └── IButtonListener.java # 이벤트를 전달받는 리스너 인터페이스
    └── facade/
        ├── Ftp.java             # 서버 연결과 디렉터리 이동 모사
        ├── Reader.java          # 파일 읽기 모사
        ├── Writer.java          # 파일 쓰기 모사
        └── SftpClient.java      # 하위 객체를 조합한 파사드
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

### 4. 데코레이터 패턴 (Decorator)

기존 객체를 같은 인터페이스의 객체로 감싸서 기능을 덧붙이는 패턴입니다. 이 예제에서는 기본 자동차의 가격에 모델별 추가 가격을 더합니다.

- `ICar`는 가격을 반환하는 `getPrice()`와 가격을 출력하는 `showPrice()`를 정의합니다.
- `Audi`는 생성자로 받은 기본 가격을 저장합니다.
- `AudiDecorator`는 `ICar` 객체를 보관하고, `getPrice()`에서 감싼 객체의 가격에 `modelPrice`를 더합니다. `showPrice()`는 모델명과 합산 가격을 출력합니다.
- `A3`, `A4`, `A5`는 `AudiDecorator`를 상속해 각각 1000, 2000, 3000의 추가 가격을 지정합니다.

`DecoratorMain`은 기본 가격이 1000인 `Audi` 하나를 만들고, 각 모델의 데코레이터로 개별적으로 감쌉니다.

| 객체 | 기본 가격 | 추가 가격 | 최종 가격 |
| --- | ---: | ---: | ---: |
| `Audi` | 1000 | 0 | 1000 |
| `A3` | 1000 | 1000 | 2000 |
| `A4` | 1000 | 2000 | 3000 |
| `A5` | 1000 | 3000 | 4000 |

가격은 패턴 설명을 위한 예시 값입니다. 각 데코레이터는 원본 `Audi`의 가격을 변경하지 않으므로 모델별 추가 가격이 서로 누적되지 않습니다. 데코레이터도 `ICar`를 구현하므로 다른 데코레이터를 감싸는 중첩 구성도 가능하지만, 현재 실행 예제는 각 모델을 독립적으로 구성합니다.

### 5. 옵저버 패턴 (Observer)

객체의 상태 변화나 이벤트가 발생하면 등록된 관찰자에게 알려 주는 패턴입니다. 이 예제에서는 버튼 클릭 메시지를 리스너에게 전달합니다.

- `Button`은 이벤트를 발생시키는 주체로, `addListener()`로 전달받은 `IButtonListener`를 보관합니다.
- `IButtonListener`는 이벤트 문자열을 받는 `clickEvent(String event)`를 정의합니다.
- `ObserverMain`은 익명 클래스로 리스너를 구현하고, 전달받은 이벤트를 콘솔에 출력하도록 등록합니다.
- `Button.click(message)`를 호출하면 등록된 리스너의 `clickEvent(message)`가 실행됩니다. 버튼은 리스너의 구체적인 처리 방식 대신 인터페이스에 의존합니다.

실행 흐름은 `리스너 등록 → 버튼 클릭 → 리스너 호출 → 메시지 출력`입니다. `ObserverMain`은 버튼을 네 번 클릭해 각 메시지가 순서대로 전달되는 것을 보여줍니다. GUI 없이 메서드 호출로 클릭 이벤트를 흉내 내는 예제이며, 이벤트 처리는 같은 스레드에서 동기적으로 실행됩니다.

현재 구현은 리스너 한 개를 저장합니다. `addListener()`를 다시 호출하면 기존 리스너가 교체되며, 여러 리스너에게 알리는 기능이나 등록 해제 기능은 구현하지 않았습니다. `click()`은 등록된 리스너를 바로 호출하므로 먼저 `addListener()`로 등록해야 합니다.

### 6. 파사드 패턴 (Facade)

여러 하위 객체를 사용하는 복잡한 절차를 간단한 인터페이스로 제공하는 패턴입니다. 이 예제에서는 서버 연결, 디렉터리 이동, 파일 읽기·쓰기를 `SftpClient`로 묶습니다.

- `Ftp`는 서버 연결, 디렉터리 이동, 연결 종료를 담당합니다.
- `Reader`와 `Writer`는 각각 파일 연결, 읽기 또는 쓰기, 연결 종료를 담당합니다.
- `SftpClient`는 세 객체를 보관하고 작업을 위임하는 파사드입니다. 기존 객체를 생성자로 전달받거나, 접속 정보와 파일명으로 내부에서 생성할 수 있습니다.
- `FacadeMain`은 `SftpClient`를 생성한 뒤 `connect() → write() → read() → disConnect()` 순서로 호출합니다.

| 파사드 메서드 | 내부 호출 순서 |
| --- | --- |
| `connect()` | `ftp.connect()` → `ftp.moveDirectory()` → `writer.fileConnect()` → `reader.fileConnect()` |
| `write()` | `writer.write()` |
| `read()` | `reader.fileRead()` |
| `disConnect()` | `writer.fileDisconnect()` → `reader.fileDisconnect()` → `ftp.disConnect()` |

호출하는 쪽은 각 하위 객체의 연결 절차를 직접 나열할 필요 없이 파사드의 메서드로 작업을 수행합니다. `FacadeMain`에는 파사드 적용 전 각 객체를 직접 사용하는 코드도 주석으로 남아 있어 구조를 비교할 수 있습니다.

현재 클래스들은 모든 동작을 콘솔 메시지로 표현합니다. `SftpClient`라는 이름을 사용하지만 실제 FTP·SFTP 통신이나 파일 생성·읽기·쓰기는 수행하지 않으므로 별도의 서버나 `text.tmp` 파일을 준비할 필요가 없습니다.

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
  ch1-design-pattern/src/proxy/aop/*.java \
  ch1-design-pattern/src/decorator/*.java \
  ch1-design-pattern/src/observer/*.java \
  ch1-design-pattern/src/facade/*.java
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

데코레이터 예제:

```bash
java -cp out/design-pattern DecoratorMain
```

```text
audi 의 가격은 1000원 입니다.
A3 의 가격은 2000원 입니다.
A4 의 가격은 3000원 입니다.
A5 의 가격은 4000원 입니다.
```

옵저버 예제:

```bash
java -cp out/design-pattern ObserverMain
```

현재 소스의 메시지 문자열을 그대로 출력합니다.

```text
메시지 전댤 : click1
메시지 전댤 : click2
메시지 전댤 : click3
메시지 전댤 : click4
```

파사드 예제:

```bash
java -cp out/design-pattern FacadeMain
```

```text
FTP Host : localhost Port : 22 로 연결 합니다.
path : /home/etc 로 이동합니다.
Writer text.tmp 로 연결합니다.
Reader text.tmp 로 연결합니다.
Writer text.tmp 로 파일쓰기를 합니다.
Reader text.tmp 의 내용을 읽어 옵니다.
Writer text.tmp 로 연결 종료 합니다.
Reader text.tmp 로 연결 종료 합니다.
FTP 연결을 종료합니다.
```

컴파일 결과는 Git에서 제외되는 `out/` 디렉터리에 생성됩니다.

### IntelliJ IDEA

1. 저장소 루트 폴더를 프로젝트로 엽니다.
2. **File → Project Structure → Project SDK**를 JDK 26으로 설정합니다.
3. `ch1-design-pattern/src`가 소스 루트로 인식되는지 확인합니다. 인식되지 않으면 해당 폴더에서 **Mark Directory as → Sources Root**를 선택합니다.
4. `SingletonMain`, `AdapterMain`, `ProxyMain`, `DecoratorMain`, `ObserverMain`, `FacadeMain` 중 실행할 클래스의 `main()` 옆 실행 버튼을 누릅니다.

여섯 예제는 `static void main()` 형태의 진입점을 사용합니다. 실행 문제가 발생하면 IDE의 프로젝트 SDK와 터미널의 JDK 버전이 위 환경과 일치하는지 확인합니다.
