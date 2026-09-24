package org.honginsung.response.controller;

import org.honginsung.response.dto.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class PageController {

    @RequestMapping("/main")
    public String main() {
        return "main.html";
    }

    // Response Entity
    @ResponseBody
    @GetMapping("/user")
    public User user() {
        var user = new User(); // Java 11 버전부터 추가된 타입 추론 변수

        user.setName("HongInSung");
        user.setAddress("FastCampus");
        user.setPhoneNumber("010-2341-4532");
//        user.setAge(0);

        return user;
    }
}
