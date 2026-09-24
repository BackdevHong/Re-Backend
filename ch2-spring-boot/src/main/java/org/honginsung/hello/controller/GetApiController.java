package org.honginsung.hello.controller;

import org.honginsung.hello.dto.UserRequest;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/get")
public class GetApiController {

    @GetMapping(path = "/hello") // http://localhost:8080/api/get/hello
    public String getHello() {
        return "get hello";
    }

    @RequestMapping(path = "/hi", method = RequestMethod.GET) // http://localhost:8080/api/get/hi
    public String hi() {
        return "get hi";
    }

    @GetMapping("/path-variable/{name}") // http://localhost:8080/api/get/path-variable/{name}
    public String pathVariable(@PathVariable(name = "name") String pathName) {
        System.out.println("PathVariable : " + pathName);
        return pathName;
    }

    @GetMapping(path = "/query-param") // http://localhost:8080/api/get/query-param?name=hong&age=13
    public String queryParam(@RequestParam Map<String, String> queryParam) {
        StringBuilder sb = new StringBuilder();
        queryParam.forEach((key, value) -> {
            System.out.println(key);
            System.out.println(value);
            System.out.println("\n");

            sb.append(key).append(" = ").append(value).append("\n");
        });
        return sb.toString();
    }

    @GetMapping(path = "/query-param02")
    public String queryParam02(@RequestParam String name, @RequestParam String email, @RequestParam int age) {
        System.out.println(name);
        System.out.println(email);
        System.out.println(age);
        return name + " " + email + " " + age;
    }

    @GetMapping(path = "/query-param03")
    public String queryParam03(UserRequest userRequest) {
        System.out.println(userRequest.getName());
        System.out.println(userRequest.getEmail());
        System.out.println(userRequest.getAge());
        return userRequest.toString();
    }
}
