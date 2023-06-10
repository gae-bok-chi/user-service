package com.gaebokchi.userservice.handler;

import com.gaebokchi.userservice.service.UserService;
import com.gaebokchi.userservice.utils.JwtTokenProvider;
import com.gaebokchi.userservice.vo.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
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

    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;
    @Autowired
    private Environment env;

    @SuppressWarnings("RedundantThrows")
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

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
        return jwtTokenProvider.generateAccessToken(Map.of("username", email, "roles", authorities),
                email, jwtTokenProvider.getTokenExpiration(jwtTokenProvider.getAccessTokenExpirationSeconds()));
    }

    private String delegateRefreshToken(String email) {
        return jwtTokenProvider.generateRefreshToken(
                email, jwtTokenProvider.getTokenExpiration(jwtTokenProvider.getRefreshTokenExpirationSeconds()));
    }

    private URI createURI(String accessToken, String refreshToken) {
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("access_token", accessToken);
        queryParams.add("refresh_token", refreshToken);

        return UriComponentsBuilder.newInstance()
                .scheme("http")
//                .host("gaebokchi.duckdns.org")
                .host(env.getProperty("LOGIN_REDIRECT_URL"))
                .port(8080).path("/user-service/home").queryParams(queryParams)
                .build().toUri();
    }
}
