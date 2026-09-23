package singleton;

public class SocketClient {

    // 자기 자신을 객체로 가지고 있어야 함 ( static 이여야 함 )
    private static SocketClient socketClient = null;

    // 기본 생성자를 막아야 함.
    private SocketClient() {

    }

    /**
     * static 메서드로 getInstance
     * 만약 없을 경우 새로 생성, 있을 경우 해당 객체 리턴
     */
    public static SocketClient getInstance() {
        if (socketClient == null) {
            socketClient = new SocketClient();
        }

        return socketClient;
    }

    public void connect() {
        System.out.println("connect");
    }
}
