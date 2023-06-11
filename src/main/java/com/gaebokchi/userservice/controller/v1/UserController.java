package com.gaebokchi.userservice.controller.v1;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class UserController {

    private final Environment env;

    @GetMapping("/server")
    public Map<?, ?> server() {
        return Collections.singletonMap("port", env.getProperty("server.port"));
    }

    @GetMapping("/home")
    public Map<?, ?> home(@RequestParam(value = "access_token") String accessToken, @RequestParam(value = "refresh_token") String refreshToken) {
        return Map.of("access_token", accessToken, "refresh_token", refreshToken);
    }
}
