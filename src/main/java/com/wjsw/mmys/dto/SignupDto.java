package com.wjsw.mmys.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupDto {
    private String username;
    private String password;
    private boolean admin = false;
    private String adminToken = "";
}