package com.gaebokchi.userservice.vo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OAuth2Type {
    KAKAO("kakao"),
    GOOGLE("google");

    private final String value;
}
