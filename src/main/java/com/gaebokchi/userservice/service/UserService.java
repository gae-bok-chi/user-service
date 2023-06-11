package com.gaebokchi.userservice.service;

import com.gaebokchi.userservice.entity.User;
import org.springframework.security.oauth2.core.user.OAuth2User;

public interface UserService {
    @SuppressWarnings("UnusedReturnValue")
    User saveOrUpdateUser(OAuth2User oAuth2User);

    User findByEmail(String email);
}
