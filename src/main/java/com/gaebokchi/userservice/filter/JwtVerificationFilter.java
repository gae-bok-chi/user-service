package com.gaebokchi.userservice.filter;

import com.gaebokchi.userservice.entity.User;
import com.gaebokchi.userservice.exception.ExpiredJwtTokenException;
import com.gaebokchi.userservice.service.UserService;
import com.gaebokchi.userservice.utils.JwtTokenizer;
import com.gaebokchi.userservice.vo.JwtCode;
import com.gaebokchi.userservice.vo.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
public class JwtVerificationFilter extends GenericFilterBean {

    private final JwtTokenizer jwtTokenizer;
    private final UserService userService;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        log.debug("JwtVerificationFilter Start");
        String token = parseJwt((HttpServletRequest) request);
        log.debug("JwtToken = {}", token);
        if (Objects.nonNull(token)) {
            JwtCode result = jwtTokenizer.verifySignature(token);
            log.debug("JwtToken Verification Result = {}", result);
            switch (result) {
                case ACCESS -> {
                    String email = jwtTokenizer.getSubject(token);
                    User user = userService.findByEmail(email);
                    Authentication authentication = getAuthentication(user);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
                case EXPIRED -> throw new ExpiredJwtTokenException("만료된 Jwt 토큰입니다.");
                case DENIED -> throw new IllegalStateException("잘못된 인증 토큰입니다.");
            }
        } else {
            throw new IllegalStateException("인증토큰이 존재하지 않습니다.");
        }
        log.debug("JwtVerificationFilter Finish");
        chain.doFilter(request, response);
    }

    public Authentication getAuthentication(User user) {
        return new UsernamePasswordAuthenticationToken(user, "",
                List.of(new SimpleGrantedAuthority(Role.USER.getValue())));
    }


    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }
        return null;
    }
}
