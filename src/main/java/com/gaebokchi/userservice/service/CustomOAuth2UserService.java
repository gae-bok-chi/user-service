package com.gaebokchi.userservice.service;

import com.gaebokchi.userservice.exception.UnsupportedException;
import com.gaebokchi.userservice.vo.OAuth2Attributes;
import com.gaebokchi.userservice.vo.OAuth2Type;
import com.gaebokchi.userservice.vo.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.registration.ClientRegistration.ProviderDetails.UserInfoEndpoint;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
//@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);
        validateAttributes(oAuth2User.getAttributes());

        UserInfoEndpoint userInfoEndpoint = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint();
        String userInfoUri = userInfoEndpoint.getUri();
        validateUserInfoEndpoint(userInfoUri);
        /* OAuth2 서비스 id 구분코드 ( 구글, 카카오, 네이버 ) */
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        validateRegistrationId(registrationId);
        /* OAuth2 로그인 진행시 키가 되는 필드 값 (PK) (구글의 기본 코드는 "sub") */
        String userNameAttributeName = userInfoEndpoint.getUserNameAttributeName();
        validateUserNameAttributeName(userNameAttributeName);

        /* OAuth2UserService */
        OAuth2Attributes oAuth2Attributes = OAuth2Attributes.of(registrationId, userNameAttributeName, oAuth2User.getAttributes());
//        User user = saveOrUpdate(attributes);
        Map<String, Object> memberAttribute = oAuth2Attributes.convertToMap();

        System.out.println("CustomOAuth2UserService.loadUser");
        System.out.println("memberAttribute = " + memberAttribute);

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(Role.USER.getValue())),
                memberAttribute, "email");
    }

    private void validateUserNameAttributeName(String userNameAttributeName) {
        if (Objects.isNull(userNameAttributeName)) {
            throw new IllegalStateException("OAuth2의 userNameAttributeName가 없습니다.");
        }
    }

    private void validateRegistrationId(String registrationId) {
        if (!OAuth2Type.validateOAuth2Type(registrationId)) {
            throw new UnsupportedException("지원하지 않는 OAuth2Type 입니다.");
        }
    }

    private void validateUserInfoEndpoint(String userInfoUri) {
        if (Objects.isNull(userInfoUri)) {
            throw new IllegalStateException("OAuth2의 UserInfoEndpoint가 없습니다.");
        }
    }

    public void validateAttributes(Map<String, Object> attributes) {
        if (!attributes.containsKey("email") ||
                !attributes.containsKey("name") ||
                !attributes.containsKey("picture")) {
            throw new IllegalStateException("OAuth2 정보에 필요한 값이 없습니다.(%s)".formatted(attributes));
        }
    }
}
