package org.honginsung.post;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PostApplication {

    /**
     * POST API 학습용 스프링 부트 애플리케이션을 시작한다.
     *
     * @param args 애플리케이션 실행 인자
     */
    public static void main(String[] args) {
        SpringApplication.run(PostApplication.class, args);
    }
}
