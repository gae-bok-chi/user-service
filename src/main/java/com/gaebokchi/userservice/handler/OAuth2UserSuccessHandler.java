package com.gaebokchi.userservice.handler;


import com.gaebokchi.userservice.entity.User;
import com.gaebokchi.userservice.service.TokenService;
import com.gaebokchi.userservice.service.UserService;
import com.gaebokchi.userservice.utils.JwtTokenizer;
import com.gaebokchi.userservice.vo.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class OAuth2UserSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JwtTokenizer jwtTokenizer;
    private final UserService userService;
    private final TokenService tokenService;
    @Value("${domain-name}")
    private String domain;

    @Transactional
    @SuppressWarnings("RedundantThrows")
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        log.debug("oAuth2User = " + oAuth2User);
        log.debug("oAuth2User.getAttributes() = " + oAuth2User.getAttributes());

        String email = oAuth2User.getAttribute("email");
        List<Role> authorities = List.of(Role.USER);

        User user = userService.saveOrUpdateUser(oAuth2User);

        String accessToken = jwtTokenizer.generateAccessToken(Map.of("username", email, "roles", authorities),
                email, jwtTokenizer.generateTokenExpiration(jwtTokenizer.getAccessTokenExpirationSeconds()));
        String refreshToken = jwtTokenizer.generateRefreshToken(
                email, jwtTokenizer.generateTokenExpiration(jwtTokenizer.getRefreshTokenExpirationSeconds()));

        tokenService.saveOrUpdateRefreshToken(accessToken, refreshToken, Role.USER.getValue(), user);

        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("access_token", accessToken);
        queryParams.add("refresh_token", refreshToken);

        String uri = UriComponentsBuilder.newInstance()
                .scheme("http").host(domain).port(8080)
                .path("/user-service/login/result")
                .queryParams(queryParams)
                .build().toUri().toString();
        getRedirectStrategy().sendRedirect(request, response, uri);
    }
}
