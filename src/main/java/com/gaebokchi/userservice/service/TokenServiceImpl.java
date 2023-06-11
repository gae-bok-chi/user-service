package com.gaebokchi.userservice.service;

import com.gaebokchi.userservice.dto.req.RefreshRequestDto;
import com.gaebokchi.userservice.dto.res.RefreshResponseDto;
import com.gaebokchi.userservice.entity.OAuthToken;
import com.gaebokchi.userservice.entity.User;
import com.gaebokchi.userservice.exception.ExpiredJwtTokenException;
import com.gaebokchi.userservice.exception.NotFoundTokenException;
import com.gaebokchi.userservice.repository.TokenRepository;
import com.gaebokchi.userservice.utils.JwtTokenizer;
import com.gaebokchi.userservice.vo.JwtCode;
import com.gaebokchi.userservice.vo.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class TokenServiceImpl implements TokenService {

    private final JwtTokenizer jwtTokenizer;
    private final UserService userService;
    private final TokenRepository tokenRepository;

    @Override
    public OAuthToken findByEmail(String email) {
        User findUser = userService.findByEmail(email);
        Optional<OAuthToken> findToken = tokenRepository.findByUserId(findUser.getId());
        if (findToken.isEmpty()) {
            throw new NotFoundTokenException("토큰을 찾을 수 없습니다.");
        }
        return findToken.get();
    }

    @Override
    public OAuthToken saveOrUpdateRefreshToken(String accessToken, String refreshToken, String authentication, User user) {
        OAuthToken oAuthToken = tokenRepository.findByUserId(user.getId())
                .map(t -> t.updateToken(accessToken, refreshToken))
                .orElse(OAuthToken.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .authentication(authentication)
                        .user(user)
                        .build());
        return tokenRepository.save(oAuthToken);
    }

    @Transactional
    @Override
    public RefreshResponseDto refreshToken(RefreshRequestDto refreshRequestDto) {
        String email = refreshRequestDto.getEmail();
        String accessToken = refreshRequestDto.getAccessToken();
        String refreshToken = refreshRequestDto.getRefreshToken();
        log.info("email={}, accessToken={}. refreshToken={}", email, accessToken, refreshToken);

        JwtCode accessTokenResult = jwtTokenizer.verifySignature(accessToken);
        JwtCode refreshTokenResult = jwtTokenizer.verifySignature(refreshToken);
        if (accessTokenResult.equals(JwtCode.DENIED) || refreshTokenResult.equals(JwtCode.DENIED)) {
            throw new IllegalStateException("잘못된 토큰 입니다.");
        } else if (refreshTokenResult.equals(JwtCode.EXPIRED)) {
            throw new ExpiredJwtTokenException("Refresh 토큰이 만료되었습니다.");
        }

        OAuthToken token = findByEmail(email);
        Date date = Date.from(Instant.now().plus(5, ChronoUnit.MINUTES));

        if (!token.getRefreshToken().equals(refreshToken)) {
            throw new IllegalStateException("Refresh 토큰이 다른 토큰입니다. 다시 로그인해주세요.");
        } else if (!token.getAccessToken().equals(accessToken) ||
                jwtTokenizer.getExpiration(token.getRefreshToken()).before(date)) {
            log.info("token expire : {}", jwtTokenizer.getExpiration(token.getRefreshToken()));
            log.info("expire checker : {}", date);
            log.info("re gen refresh token");
            refreshToken = jwtTokenizer.generateRefreshToken(
                    email, jwtTokenizer.generateTokenExpiration(jwtTokenizer.getRefreshTokenExpirationSeconds()));
        }
        log.info("re gen access token");
        accessToken = jwtTokenizer.generateAccessToken(jwtTokenizer.generateClaims(email, Role.USER),
                email, jwtTokenizer.generateTokenExpiration(jwtTokenizer.getAccessTokenExpirationSeconds()));
        token.updateToken(accessToken, refreshToken);

        return RefreshResponseDto.builder()
                .email(email)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
