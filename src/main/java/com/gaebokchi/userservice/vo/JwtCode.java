package com.gaebokchi.userservice.vo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum JwtCode {
    ACCESS("access"),
    EXPIRED("expired"),
    DENIED("denied");

    private final String value;
}
