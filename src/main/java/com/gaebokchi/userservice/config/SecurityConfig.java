package com.gaebokchi.userservice.config;

import com.gaebokchi.userservice.filter.JwtVerificationFilter;
import com.gaebokchi.userservice.handler.OAuth2UserSuccessHandler;
import com.gaebokchi.userservice.service.UserService;
import com.gaebokchi.userservice.utils.JwtTokenProvider;
import com.gaebokchi.userservice.vo.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.oauth2.client.CommonOAuth2Provider;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    //    @SuppressWarnings("unused")
//    private final CustomOAuth2UserService customOAuth2UserService;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;
    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String clientSecret;
    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String redirectUri;

    @Bean
    public BCryptPasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.httpBasic().disable()
                .cors().disable()
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/api/v1/**").hasRole(Role.USER.getValue())//.hasAuthority(Role.USER.getValue())
                .antMatchers("/admin/**").hasRole(Role.ADMIN.getValue())//.hasAuthority(Role.ADMIN.getValue())
                .anyRequest().permitAll()

                .and()
                .logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/logout_success")
                .and()
                .addFilterBefore(new JwtVerificationFilter(jwtTokenProvider, userService), UsernamePasswordAuthenticationFilter.class)
                .oauth2Login()
//                .loginPage("/login")
                .successHandler(oAuth2UserSuccessHandler());
//                .failureHandler()
//                .userInfoEndpoint().userService(customOAuth2UserService);

        return http.build();
    }

    @Bean
    public OAuth2UserSuccessHandler oAuth2UserSuccessHandler() {
        return new OAuth2UserSuccessHandler(jwtTokenProvider, userService);
    }

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {
        return new InMemoryClientRegistrationRepository(clientRegistration());
    }

    private ClientRegistration clientRegistration() {
        return CommonOAuth2Provider
                .GOOGLE
                .getBuilder("google")
                .clientId(clientId)
                .clientSecret(clientSecret)
                .redirectUri(redirectUri)
                .build();
    }
}
