package com.wjsw.mmys.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.wjsw.mmys.service.KakaoUserService;
import com.wjsw.mmys.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final KakaoUserService kakaoUserService;


    // 회원 로그인 페이지
    @GetMapping("/user/login")
    public String login() {
        return "login";
    }

    // 회원 가입 페이지
    @GetMapping("/user/signup")
    public String signup() {
        return "signup";
    }

    // 회원 가입 요청 처리
//    @PostMapping("/user/signup")
//    public String registerUser(SignupDto requestDto) {
//        userService.registerUser(requestDto);
//        return "redirect:/user/login";
//    }

    @GetMapping("/user/kakao/callback")
    public String kakaoLogin(@RequestParam String code) throws JsonProcessingException {
        kakaoUserService.kakaoLogin(code);
        return "redirect:/";
    }
}