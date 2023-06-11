package com.gaebokchi.userservice.controller;


import com.gaebokchi.userservice.dto.req.RefreshRequestDto;
import com.gaebokchi.userservice.dto.res.RefreshResponseDto;
import com.gaebokchi.userservice.service.TokenService;
import com.gaebokchi.userservice.utils.JwtTokenizer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collections;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
public class CommonController {

    private final Environment env;
    private final TokenService tokenService;

    @GetMapping("/server")
    public Map<?, ?> server() {
        return Collections.singletonMap("port", env.getProperty("server.port"));
    }

    @PostMapping("/refresh")
    public ResponseEntity<RefreshResponseDto> refresh(@RequestBody @Valid RefreshRequestDto refreshRequestDto) {
        log.info("try refresh");
        RefreshResponseDto refreshResponseDto = tokenService.refreshToken(refreshRequestDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .header(JwtTokenizer.ACCESS_TOKEN_HEADER, refreshResponseDto.getAccessToken())
                .header(JwtTokenizer.REFRESH_TOKEN_HEADER, refreshResponseDto.getRefreshToken())
                .body(refreshResponseDto);
    }

    @GetMapping("/login/result")
    public ResponseEntity<?> login(@RequestParam(value = "access_token") String accessToken, @RequestParam(value = "refresh_token") String refreshToken) {
        return ResponseEntity.ok()
                .header(JwtTokenizer.ACCESS_TOKEN_HEADER, accessToken)
                .header(JwtTokenizer.REFRESH_TOKEN_HEADER, refreshToken)
                .body(Collections.singletonMap("result", "success"));
    }
}
