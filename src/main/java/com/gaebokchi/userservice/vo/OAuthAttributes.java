package com.gaebokchi.userservice.vo;

import com.gaebokchi.userservice.entity.User;
import com.gaebokchi.userservice.exception.UnsupportedException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class OAuthAttributes {
    private Map<String, Object> attributes;
    private String nameAttributeKey;
    private String name;
    private String email;
    private String picture;
    private Role role;

    public static OAuthAttributes of(String registrationId,
                                     String userNameAttributeName,
                                     Map<String, Object> attributes) {
        if (registrationId.equals(OAuth2Type.KAKAO.getValue())) {
            throw new UnsupportedException("아직 지원하지 않는 로그인 방식입니다.");
//            return ofKakao(userNameAttributeName, attributes);
        } else if (registrationId.equals(OAuth2Type.GOOGLE.getValue())) {
            return ofGoogle(userNameAttributeName, attributes);
        } else {
            throw new UnsupportedException("지원하지 않는 로그인 방식입니다..");
        }
    }


    private static OAuthAttributes ofGoogle(String userNameAttributeName, Map<String, Object> attributes) {
        return OAuthAttributes.builder()
                .name((String) attributes.get("name"))
                .email((String) attributes.get("email"))
                .picture((String) attributes.get("picture"))
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .build();
    }

//    private static OAuthAttributes ofKakao(String userNameAttributeName,
//                                          Map<String, Object> attributes) {
//        return OAuthAttributes.builder()
//                .name((String) attributes.get("name"))
//                .email((String) attributes.get("email"))
//                .picture((String) attributes.get("picture"))
//                .attributes(attributes)
//                .nameAttributeKey(userNameAttributeName)
//                .build();
//    }

    public User toEntity() {
        return User.builder()
                .name(name)
                .email(email)
                .picture(picture)
                .role(Role.USER)
                .build();
    }
}
