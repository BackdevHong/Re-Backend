package org.honginsung.aop.controller;


import org.honginsung.aop.annotation.Decode;
import org.honginsung.aop.annotation.Timer;
import org.honginsung.aop.dto.User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class RestApiController {

    @GetMapping("/get/{id}")
    public String get(@PathVariable long id, @RequestParam String name) {
//        System.out.println("get method");
//        System.out.println("get method : " + id);
//        System.out.println("get method : " + name); 로그를 각 메서드마다 찍기는 너무 힘듬
        return id + " " + name;
    }

    @PostMapping("/post")
    public User post(@RequestBody User user) {
//        System.out.println("post method : " + user);
        return user;
    }

    @Timer
    @DeleteMapping("/delete")
    public void delete() throws InterruptedException {
        // db logic
        Thread.sleep(1000 * 2);
    }

    @Decode
    @PutMapping("/put")
    public User put(@RequestBody User user) {
        System.out.println("put");
        System.out.println(user);
        return user;
    }
}
