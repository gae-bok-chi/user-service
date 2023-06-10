package com.gaebokchi.userservice.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.security.Key;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

@Getter
@Component
public class JwtTokenProvider {

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

    public String generateAccessToken(Map<String, Object> claims, String subject, Date expiration) {
        return Jwts.builder()
                .addClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date())
                .setExpiration(expiration)
                .signWith(getKeyFromSecretKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(String subject, Date expiration) {
        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(new Date())
                .setExpiration(expiration)
                .signWith(getKeyFromSecretKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public Date getTokenExpiration(int tokenExpirationSeconds) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, tokenExpirationSeconds);
        return calendar.getTime();
    }

    private Key getKeyFromSecretKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    public boolean verifySignature(String jws) {
        try {
            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(getKeyFromSecretKey())
                    .build()
                    .parseClaimsJws(jws);
            return claims.getBody()
                    .getExpiration()
                    .after(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    public String getSubject(String jws) {
        return Jwts.parserBuilder()
                .setSigningKey(getKeyFromSecretKey())
                .build()
                .parseClaimsJws(jws)
                .getBody()
                .getSubject();
    }
}
