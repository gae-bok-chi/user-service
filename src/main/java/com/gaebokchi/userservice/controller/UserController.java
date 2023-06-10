package com.gaebokchi.userservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
public class UserController {

    @GetMapping("/health_check")
    public Map<?, ?> healthCheck() {
        return Collections.singletonMap("status", "up");
    }

    @GetMapping("/login_success")
    public Map<?, ?> loginCheck() {
        return Collections.singletonMap("login", "success");
    }

    @GetMapping("/logout_success")
    public Map<?, ?> logoutCheck() {
        return Collections.singletonMap("logout", "success");
    }

    @GetMapping("/hello-oauth2")
    public String home() {
        OAuth2User oAuth2User = (OAuth2User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        System.out.println(oAuth2User);
        System.out.println("User's email in Google: " + oAuth2User.getAttributes().get("email"));
        return "home-oauth2";
    }
}
