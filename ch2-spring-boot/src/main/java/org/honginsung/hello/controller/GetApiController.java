package org.honginsung.hello.controller;

import org.springframework.web.bind.annotation.*;

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


}
