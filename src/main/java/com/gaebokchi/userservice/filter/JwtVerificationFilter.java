package com.gaebokchi.userservice.filter;

import com.gaebokchi.userservice.entity.User;
import com.gaebokchi.userservice.service.UserService;
import com.gaebokchi.userservice.utils.JwtTokenProvider;
import com.gaebokchi.userservice.vo.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
public class JwtVerificationFilter extends GenericFilterBean {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String token = ((HttpServletRequest) request).getHeader("Auth");

        if (Objects.nonNull(token) && jwtTokenProvider.verifySignature(token)) {
            String email = jwtTokenProvider.getSubject(token);
            User user = userService.findByEmail(email);

            Authentication authentication = getAuthentication(user);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        chain.doFilter(request, response);
    }

    public Authentication getAuthentication(User user) {
        return new UsernamePasswordAuthenticationToken(user, "",
                List.of(new SimpleGrantedAuthority(Role.USER.getValue())));
    }
}
