package com.wjsw.mmys.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wjsw.mmys.dto.KakaoUserInfoDto;
import com.wjsw.mmys.model.User;
import com.wjsw.mmys.model.UserRoleEnum;
import com.wjsw.mmys.repository.UserRepository;
import com.wjsw.mmys.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Service
public class KakaoUserService {

    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;

    @Value("${kakaosecretkey}")
    private String kakaoKey;

    @Autowired
    public KakaoUserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void kakaoLogin(String code) throws JsonProcessingException {

        String accessToken = getAccessToken(code);

        KakaoUserInfoDto kakaoUserInfo = getKakaoUserInfo(accessToken);

        User kakaoUser = registerKakaoUser(kakaoUserInfo);

        forceLogin(kakaoUser);
    }


    private String getAccessToken(String code) throws JsonProcessingException {

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", kakaoKey);
        body.add("redirect_uri", "http://localhost:8080/user/kakao/callback");
        body.add("code", code);

        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest =
                new HttpEntity<>(body, headers);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                kakaoTokenRequest,
                String.class
        );

        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        return jsonNode.get("access_token").asText();
    }


    private KakaoUserInfoDto getKakaoUserInfo(String accessToken) throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        HttpEntity<MultiValueMap<String, String>> kakaoUserInfoRequest = new HttpEntity<>(headers);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
                kakaoUserInfoRequest,
                String.class
        );

        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        Long id = jsonNode.get("id").asLong();
        String nickname = jsonNode.get("properties").get("nickname").asText();
        String email = jsonNode.get("kakao_account").get("email").asText();

        System.out.println("카카오 사용자 정보: " + id + ", " + nickname + ", " + email);
        return new KakaoUserInfoDto(id, nickname, email);
    }

    private User registerKakaoUser(KakaoUserInfoDto kakaoUserInfo) {
        Long kakaoId = kakaoUserInfo.getId();
        User kakaoUser = userRepository.findByKakaoId(kakaoId).orElse(null);

        if (kakaoUser == null) {
            String kakaoEmail = kakaoUserInfo.getEmail();
            User sameEmailUser = userRepository.findByEmail(kakaoEmail).orElse(null);
            if (sameEmailUser != null) {
                kakaoUser = sameEmailUser;

                kakaoUser.setKakaoId(kakaoId);
            } else {

                String nickname = kakaoUserInfo.getNickname();

                String password = UUID.randomUUID().toString();
                String encodedPassword = passwordEncoder.encode(password);

                String email = kakaoUserInfo.getEmail();

                UserRoleEnum role = UserRoleEnum.USER;

                kakaoUser = new User(nickname, encodedPassword, role, kakaoId);
            }
            userRepository.save(kakaoUser);
        }
        return kakaoUser;
    }

    private void forceLogin(User kakaoUser) {
        UserDetails userDetails = new UserDetailsImpl(kakaoUser);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
