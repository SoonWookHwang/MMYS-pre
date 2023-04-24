package com.wjsw.mmys.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@Service
public class KakaoOAuth2Service {
    @Value("${rest-api-key}")
    private static String CLIENT_ID;
    private static final String REDIRECT_URI = "http://localhost:8080/home";

    public String getLoginUrl() {
        return UriComponentsBuilder.newInstance()
                .scheme("https").host("kauth.kakao.com").path("/oauth/authorize")
                .queryParam("client_id", CLIENT_ID)
                .queryParam("redirect_uri", REDIRECT_URI)
                .queryParam("response_type", "code")
                .queryParam("scope", "profile")
                .build().toUriString();
    }

    public String getAccessToken(String code) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "<https://kauth.kakao.com/oauth/token>";

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("grant_type", "authorization_code")
                .queryParam("client_id", CLIENT_ID)
                .queryParam("redirect_uri", REDIRECT_URI)
                .queryParam("code", code);

        String response = restTemplate.postForObject(builder.toUriString(), null, String.class);

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> map = null;

        try {
            map = objectMapper.readValue(response, new TypeReference<Map<String, Object>>() {});
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return map.get("access_token").toString();
    }

    public String getNickname(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "<https://kapi.kakao.com/v2/user/me?access_token=>" + accessToken;

        String response = restTemplate.getForObject(url, String.class);

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> map = null;

        try {
            map = objectMapper.readValue(response, new TypeReference<Map<String, Object>>() {});
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return ((Map<String, Object>) map.get("properties")).get("nickname").toString();
    }
}

