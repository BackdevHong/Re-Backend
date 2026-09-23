import adapter.*;

public class AdapterMain {
    static void main() {
        HairDryer hairDryer = new HairDryer();
        connect(hairDryer);

        // Cleaner cleaner = new Cleaner();
        // connect(cleaner); 안맞음, 110v에 220v를 넣을 수 없음
        // 이것을 변환시켜 주는 것이 어뎁터임

        Cleaner cleaner = new Cleaner();
        // 어뎁터로 중간 연결
        Electronic110V adapter = new SocketAdapter(cleaner);
        connect(adapter);

        AirConditioner airConditioner = new AirConditioner();
        // 어뎁터로 중간 연결
        Electronic110V airAdapter = new SocketAdapter(airConditioner);
        connect(airAdapter);
    }

    public static void connect(Electronic110V electronic110V) {
        electronic110V.powerOn();
    }
}
