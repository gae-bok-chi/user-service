package com.gaebokchi.userservice.utils;

import com.gaebokchi.userservice.vo.JwtCode;
import com.gaebokchi.userservice.vo.Role;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.security.Key;
import java.util.*;

@Slf4j
@Getter
@Component
public class JwtTokenizer {
    public final static String ACCESS_TOKEN_HEADER = "Authorization";
    public final static String REFRESH_TOKEN_HEADER = "Authorization-refresh";

    @Value("${jwt.key.secret}")
    private String secretKey;
    @Value("${jwt.access-token-expiration-seconds}")
    private int accessTokenExpirationSeconds;
    @Value("${jwt.refresh-token-expiration-seconds}")
    private int refreshTokenExpirationSeconds;

    @PostConstruct
    public void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    public Map<String, Object> generateClaims(String email, Role role) {
        return Map.of("username", email, "roles", List.of(role));
    }

    public String generateAccessToken(Map<String, Object> claims, String subject, Date expiration) {
        return Jwts.builder()
                .addClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date())
                .setExpiration(expiration)
                .signWith(generateKeyFromSecretKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(String subject, Date expiration) {
        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(new Date())
                .setExpiration(expiration)
                .signWith(generateKeyFromSecretKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public Date generateTokenExpiration(int tokenExpirationSeconds) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, tokenExpirationSeconds);
        return calendar.getTime();
    }

    private Key generateKeyFromSecretKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    public JwtCode verifySignature(String jws) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(generateKeyFromSecretKey())
                    .build()
                    .parseClaimsJws(jws);
            return JwtCode.ACCESS;
        } catch (ExpiredJwtException e) {
            // 만료된 경우에는 refresh token을 확인하기 위해
            return JwtCode.EXPIRED;
        } catch (JwtException | IllegalArgumentException e) {
            log.info("JwtException : {}", e.getMessage());
        }
        return JwtCode.DENIED;
    }

    public String getSubject(String jws) {
        return Jwts.parserBuilder()
                .setSigningKey(generateKeyFromSecretKey())
                .build()
                .parseClaimsJws(jws)
                .getBody()
                .getSubject();
    }

    public Date getExpiration(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(generateKeyFromSecretKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
    }
}
