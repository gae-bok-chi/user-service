package com.gaebokchi.userservice.config;

import com.gaebokchi.userservice.service.CustomOAuth2UserService;
import com.gaebokchi.userservice.vo.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomOAuth2UserService customOauth2UserService;

    @Bean
    public BCryptPasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .httpBasic().disable()
                .cors().disable()
                .csrf().disable()
//                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                .authorizeRequests()
                .antMatchers("/api/v1/**").hasAuthority(Role.USER.getValue())
                .antMatchers("/admin/**").hasAuthority("ADMIN")
                .anyRequest().permitAll()

//                .and()
//                .formLogin()
//                .loginPage("/auth/login")
//                .loginProcessingUrl("/auth/loginProc")
//                .defaultSuccessUrl("/login_success")
                .and()
                .logout()
                .logoutSuccessUrl("/")
                .and()
                .oauth2Login()
                .defaultSuccessUrl("/login_success")
                .userInfoEndpoint()
                .userService(customOauth2UserService);

        return http.build();
    }
}
