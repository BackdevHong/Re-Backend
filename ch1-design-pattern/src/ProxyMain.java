import proxy.aop.AopBrowser;
import proxy.IBrowser;

import java.util.concurrent.atomic.AtomicLong;

public class ProxyMain {
    static void main() {
//        Browser browser = new Browser("www.google.com");
//        browser.show();
//        browser.show();
//        browser.show();
//        browser.show();
//        browser.show();

        // 캐시 기능이 들어간 프록시 패턴
//        BrowserProxy browser = new BrowserProxy("www.google.com");
//        browser.show();
//        browser.show();
//        browser.show();
//        browser.show();
//        browser.show();


        AtomicLong start = new AtomicLong();
        AtomicLong end = new AtomicLong();

        // Aop
        IBrowser aopBrowser = new AopBrowser("www.google.com",
                () -> {
                    System.out.println("before");
                    start.set(System.currentTimeMillis());
                },
                () -> {
                    long now = System.currentTimeMillis();
                    end.set(now - start.get());
                }
        );

        aopBrowser.show();
        System.out.println(end.get());

        aopBrowser.show();
        System.out.println(end.get());
    }
}
