package com.wjsw.mmys.service;

import com.wjsw.mmys.dto.SignupDto;
import com.wjsw.mmys.model.User;
import com.wjsw.mmys.model.UserRoleEnum;
import com.wjsw.mmys.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private static final String ADMIN_TOKEN = "starbucks-silim";
    @Transactional
    public User registerUser(SignupDto signupDto) {
        String username = signupDto.getUsername();
        Optional<User> foundUser = userRepository.findByUsername(username);
        if(foundUser.isPresent()) {
            throw new IllegalArgumentException("중복된 사용자 ID가 존재합니다.");
        }
        String password = passwordEncoder.encode(signupDto.getPassword());

        UserRoleEnum role = UserRoleEnum.USER;
        if(signupDto.isAdmin()) {
            if(!signupDto.getAdminToken().equals(ADMIN_TOKEN)) {
                throw new IllegalArgumentException("관리자 암호가 틀려 등록 불가");
            }
            role = UserRoleEnum.ADMIN;
        }

        User user = new User(username,password,role);
        userRepository.save(user);

        return user;
    }
}
