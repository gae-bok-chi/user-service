package com.gaebokchi.userservice.utils;

import com.gaebokchi.userservice.vo.JwtCode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@SpringBootTest
class JwtTokenizerTest {

    @Autowired
    private JwtTokenizer jwtTokenizer;

    @DisplayName("return JwtCode.ACCESS when jws verify")
    @Test
    public void verifyAccessTest() {
        String accessToken = jwtTokenizer.generateAccessToken(
                Map.of("claim1", "A", "claim2", "B"),
                "subject",
                jwtTokenizer.generateTokenExpiration(jwtTokenizer.getAccessTokenExpirationSeconds()));

        Assertions.assertEquals(JwtCode.ACCESS, jwtTokenizer.verifySignature(accessToken));
    }

    @DisplayName("return JwtCode.EXPIRED when jws verify")
    @Test
    public void verifyExpiredTest() throws InterruptedException {
        String accessToken = jwtTokenizer.generateAccessToken(Map.of("claim1", "A", "claim2", "B"),
                "subject",
                jwtTokenizer.generateTokenExpiration(1));
        Assertions.assertEquals(JwtCode.ACCESS, jwtTokenizer.verifySignature(accessToken));

        TimeUnit.MILLISECONDS.sleep(Duration.ofSeconds(2).toMillis());

        Assertions.assertEquals(JwtCode.EXPIRED, jwtTokenizer.verifySignature(accessToken));
    }

    @DisplayName("return JwtCode.DENIED when jws verify")
    @Test
    public void verifyDeniedTest() {
        String accessToken = jwtTokenizer.generateAccessToken(Map.of("claim1", "A", "claim2", "B"),
                "subject",
                jwtTokenizer.generateTokenExpiration(1));

        Assertions.assertEquals(JwtCode.DENIED, jwtTokenizer.verifySignature("A" + accessToken + "A"));
    }
}