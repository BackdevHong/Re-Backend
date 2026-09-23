import observer.Button;
import observer.IButtonListener;

public class ObserverMain {
    static void main() {
        Button button = new Button("버튼");
        button.addListener(new IButtonListener() {
            @Override
            public void clickEvent(String event) {
                System.out.println(event);
            }
        });
        button.click("메시지 전댤 : click1");
        button.click("메시지 전댤 : click2");
        button.click("메시지 전댤 : click3");
        button.click("메시지 전댤 : click4");
    }
}
