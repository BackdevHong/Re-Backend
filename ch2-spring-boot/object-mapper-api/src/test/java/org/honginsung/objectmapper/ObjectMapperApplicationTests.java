package org.honginsung.objectmapper;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest
public class ObjectMapperApplicationTests {

    @Test
    void contextLoads() {
        System.out.println("--------------");

        // Text Json <--> Object

        // controller req json(text) -> object
        // response object -> json(text)

        var objectMapper = new ObjectMapper();

        // object --> text
        // get method를 활용한다.
        var user = new User("Hong", 20, "010-1234-1234");
        var text = objectMapper.writeValueAsString(user);
        System.out.println(text);

        // text --> object
        // default 생성자를 필요로 함
        var objectUser = objectMapper.readValue(text, User.class);
        System.out.println(objectUser);
    }
}
