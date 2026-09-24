package org.honginsung.response.controller;

import org.honginsung.response.dto.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ApiController {
    // Text
    // 사실 이렇게 쓰는 경우는 거의 없다
    @GetMapping("/text")
    public String text(@RequestParam String account) {
        return account;
    }

    // Json
    // req -> object mapper -> object -> method -> object -> object mapper -> json -> response
    @PostMapping("/json")
    public User json(@RequestBody User user) {
        return user;
    }

    // ResponseEntity
    @PutMapping("/put")
    public ResponseEntity<User> put(@RequestBody User user) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(user);
    }


}
