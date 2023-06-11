package com.gaebokchi.userservice.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gaebokchi.userservice.entity.User;
import com.gaebokchi.userservice.service.UserService;
import com.gaebokchi.userservice.utils.JwtTokenizer;
import com.gaebokchi.userservice.vo.JwtCode;
import com.gaebokchi.userservice.vo.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RequiredArgsConstructor
public class JwtVerificationFilter extends GenericFilterBean {

    private final JwtTokenizer jwtTokenizer;
    private final UserService userService;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String token = parseJwt((HttpServletRequest) request);
        log.info("JwtToken = {}", token);
        if (Objects.nonNull(token)) {
            JwtCode result = jwtTokenizer.verifySignature(token);
            log.info("JwtToken Verification Result = {}", result);
            if (result.equals(JwtCode.ACCESS)) {
                String email = jwtTokenizer.getSubject(token);
                User user = userService.findByEmail(email);
                Authentication authentication = getAuthentication(user);
                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.info("JwtVerificationFilter Success Finish");
            } else if (result.equals(JwtCode.EXPIRED)) {
                HttpServletResponse res = (HttpServletResponse) response;
                res.setStatus(SC_UNAUTHORIZED);
                res.setContentType(APPLICATION_JSON_VALUE);
                res.setCharacterEncoding("utf-8");
                new ObjectMapper().writeValue(response.getWriter(), ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Collections.singletonMap("result", "Access Token이 만료되었습니다.")));
            } else {
                HttpServletResponse res = (HttpServletResponse) response;
                res.setStatus(SC_BAD_REQUEST);
                res.setContentType(APPLICATION_JSON_VALUE);
                res.setCharacterEncoding("utf-8");
                new ObjectMapper().writeValue(response.getWriter(), ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Collections.singletonMap("result", "잘못된 Token 입니다.")));
            }
        }
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
