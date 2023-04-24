package com.wjsw.mmys.controller;

//import com.wjsw.mmys.dto.SignupDto;
//import com.wjsw.mmys.model.User;
//import com.wjsw.mmys.service.KakaoOAuth2Service;
//import com.wjsw.mmys.service.UserService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PostMapping;
//
//@Controller
//@RequiredArgsConstructor
//public class KakaoOAuth2Controller {
//    private final KakaoOAuth2Service kakaoOAuth2Service;
//    private final UserService userService;
//
//    @Value("${client_id}")
//    private String restApiKey;
//
//    private String redirectUri = "/";
//
//    private String requestmsg = "/oauth/authorize?client_id="+restApiKey+"&redirect_uri="+redirectUri+"&response_type=code";
//    @GetMapping("/login")
//    public String login(Model model) {
//        return "login";
//    }
//
//    @GetMapping("/oauth2/kakao")
//    public String kakaoLogin() {
//        return kakaoOAuth2Service.getLoginUrl();
//    }
//
//    @GetMapping("/oauth2/kakao/callback")
//    public String kakaoCallback(String code) {
//        String accessToken = kakaoOAuth2Service.getAccessToken(code);
//        String nickname = kakaoOAuth2Service.getNickname(accessToken);
//
//        User user = userService.findByNickname(nickname);
//
//        if (user == null) {
//            SignupDto signupDto = new SignupDto();
//            signupDto.setNickname(nickname);
//
//            userService.save(signupDto);
//        }
//
//        return "redirect:/";
//    }
//
//    @PostMapping("/logout")
//    public String logout() {
//        return "redirect:/";
//    }
//}
