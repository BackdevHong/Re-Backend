package org.honginsung.post.controller;

import org.honginsung.post.dto.PostRequestDto;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class PostApiController {

    @PostMapping(path = "/post")
    public void post(@RequestBody Map<String, Object> requestData) {
        requestData.forEach((key, value) -> {
            System.out.println(key + " : " + value);
        });
    }

    @PostMapping(path = "/post-fix")
    public void postFix(@RequestBody PostRequestDto postRequestDto) {
        System.out.println(postRequestDto);
    }
}
