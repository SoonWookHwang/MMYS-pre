package com.wjsw.mmys.repository;

import com.wjsw.mmys.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {

    Optional<User> findByUsername(String username);
    Optional<User> findByKakaoId(Long kakaoId);
}