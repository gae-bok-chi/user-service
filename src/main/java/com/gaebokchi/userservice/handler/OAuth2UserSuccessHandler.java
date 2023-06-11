package com.gaebokchi.userservice.handler;

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
import java.net.URI;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class OAuth2UserSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenizer jwtTokenizer;
    private final UserService userService;
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

        userService.saveUser(oAuth2User);
        redirection(request, response, email, authorities);
    }

    private void redirection(HttpServletRequest request, HttpServletResponse response, String email, List<Role> authorities) throws IOException {
        String accessToken = delegateAccessToken(email, authorities);
        String refreshToken = delegateRefreshToken(email);

        String uri = createURI(accessToken, refreshToken).toString();
        getRedirectStrategy().sendRedirect(request, response, uri);
    }

    private String delegateAccessToken(String email, List<Role> authorities) {
        return jwtTokenizer.generateAccessToken(Map.of("username", email, "roles", authorities),
                email, jwtTokenizer.getTokenExpiration(jwtTokenizer.getAccessTokenExpirationSeconds()));
    }

    private String delegateRefreshToken(String email) {
        return jwtTokenizer.generateRefreshToken(
                email, jwtTokenizer.getTokenExpiration(jwtTokenizer.getRefreshTokenExpirationSeconds()));
    }

    private URI createURI(String accessToken, String refreshToken) {
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("access_token", accessToken);
        queryParams.add("refresh_token", refreshToken);

        return UriComponentsBuilder.newInstance()
                .scheme("http").host(domain).port(8080)
                .path("/user-service/home").queryParams(queryParams)
                .build().toUri();
    }
}
