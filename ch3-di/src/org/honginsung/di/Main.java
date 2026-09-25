package org.honginsung.di;

public class Main {
    static void main() {
        String url = "www.naver.com/books/it?page=10&page=20&name=spring-boot";

        Encoder encoder = new Encoder(new UrlEncoder()); // 외부에서 사용하는 객체를 주입받는 형태 - DI
        String result = encoder.encode(url);
        System.out.println(result);
    }
}
