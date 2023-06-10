package com.gaebokchi.userservice.vo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Set;

@Getter
@RequiredArgsConstructor
public enum OAuth2Type {
    KAKAO("kakao"),
    NAVER("naver"),
    GOOGLE("google");

    private final String value;

    public static final Set<OAuth2Type> set = Set.of(OAuth2Type.values());

    public static boolean validateOAuth2Type(String oAuth2Type) {
        return set.contains(OAuth2Type.valueOf(oAuth2Type));
    }

}
