package com.gaebokchi.userservice.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
class JwtTokenProviderTest {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @DisplayName("jws parsing test")
    @Test
    public void verifySignatureTest() {
        String accessToken = jwtTokenProvider.generateAccessToken(
                Map.of("claim1", "A", "claim2", "B"),
                "subject",
                jwtTokenProvider.getTokenExpiration(jwtTokenProvider.getAccessTokenExpirationSeconds()));
        assertDoesNotThrow(() -> jwtTokenProvider.verifySignature(accessToken));
    }

    @DisplayName("throw ExpiredJwtException when jws verify")
    @Test
    public void verifyExpirationTest() throws InterruptedException {
        String accessToken = jwtTokenProvider.generateAccessToken(Map.of("claim1", "A", "claim2", "B"),
                "subject",
                jwtTokenProvider.getTokenExpiration(1));
        assertDoesNotThrow(() -> jwtTokenProvider.verifySignature(accessToken));

        TimeUnit.MILLISECONDS.sleep(1500);

        Assertions.assertFalse(jwtTokenProvider.verifySignature(accessToken));
    }
}