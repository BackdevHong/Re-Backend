package org.honginsung.delete.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class DeleteApiController {

    @DeleteMapping("/delete/{userId}")
    public void delete(@PathVariable String userId, @RequestParam String account) {
        // 실제로 DB나 데이터 저장 로직이 없기에 콘솔 로그만 찍고 끝
        System.out.println(userId);
        System.out.println(account);
    }
}
