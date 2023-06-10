package com.gaebokchi.userservice.vo;

import com.gaebokchi.userservice.exception.UnsupportedException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class OAuth2Attributes {
    private Map<String, Object> attributes;
    private String attributeKey;
    private String name;
    private String email;
    private String picture;
    private Role role;

    @SuppressWarnings("EnhancedSwitchMigration")
    public static OAuth2Attributes of(String registrationId, String attributeKey, Map<String, Object> attributes) {
        switch (registrationId) {
            case "google":
                return ofGoogle(attributeKey, attributes);
            case "kakao":
                return ofKakao(attributeKey, attributes);
            case "naver":
                return ofNaver(attributeKey, attributes);
            default:
                throw new UnsupportedException("지원하지 않는 로그인 방식입니다.");
        }
    }


    private static OAuth2Attributes ofGoogle(String attributeKey, Map<String, Object> attributes) {
        return OAuth2Attributes.builder()
                .name((String) attributes.get("name"))
                .email((String) attributes.get("email"))
                .picture((String) attributes.get("picture"))
                .attributes(attributes)
                .attributeKey(attributeKey)
                .build();
    }

    @SuppressWarnings("unchecked")
    private static OAuth2Attributes ofKakao(String attributeKey, Map<String, Object> attributes) {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> kakaoProfile = (Map<String, Object>) kakaoAccount.get("profile");

        return OAuth2Attributes.builder()
                .name((String) kakaoProfile.get("nickname"))
                .email((String) kakaoAccount.get("email"))
                .picture((String) kakaoProfile.get("profile_image_url"))
                .attributes(kakaoAccount)
                .attributeKey(attributeKey)
                .build();
    }

    @SuppressWarnings("unchecked")
    private static OAuth2Attributes ofNaver(String attributeKey, Map<String, Object> attributes) {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");

        return OAuth2Attributes.builder()
                .name((String) response.get("name"))
                .email((String) response.get("email"))
                .picture((String) response.get("profile_image"))
                .attributes(response)
                .attributeKey(attributeKey)
                .build();
    }

    public Map<String, Object> convertToMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", attributeKey);
        map.put("key", attributeKey);
        map.put("provider", attributeKey);
        map.put("name", name);
        map.put("email", email);
        map.put("picture", picture);

        return map;
    }
}
