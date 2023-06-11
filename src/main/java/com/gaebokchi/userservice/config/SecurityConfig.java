package com.gaebokchi.userservice.config;

import com.gaebokchi.userservice.filter.JwtVerificationFilter;
import com.gaebokchi.userservice.handler.OAuth2UserSuccessHandler;
import com.gaebokchi.userservice.service.TokenService;
import com.gaebokchi.userservice.service.UserService;
import com.gaebokchi.userservice.utils.JwtTokenizer;
import com.gaebokchi.userservice.vo.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtTokenizer jwtTokenizer;
    private final UserService userService;
    private final TokenService tokenService;

    @Bean
    public BCryptPasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.httpBasic().disable()
                .cors().disable()
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/api/**").hasAuthority(Role.USER.getValue())//.hasAuthority(Role.USER.getValue())
                .antMatchers("/admin/**").hasAuthority(Role.ADMIN.getValue())//.hasAuthority(Role.ADMIN.getValue())
                .anyRequest().permitAll()

                .and()
                .addFilterBefore(new JwtVerificationFilter(jwtTokenizer, userService), UsernamePasswordAuthenticationFilter.class)
                .oauth2Login()
//                .loginPage("/login")
                .successHandler(new OAuth2UserSuccessHandler(jwtTokenizer, userService, tokenService));
//                .failureHandler()
//                .userInfoEndpoint().userService(customOAuth2UserService);

        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        return http.build();
    }
}
