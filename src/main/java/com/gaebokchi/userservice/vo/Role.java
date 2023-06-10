package com.gaebokchi.userservice.vo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {
    GUEST("GUEST"),
    USER("USER"),
    ADMIN("ADMIN");

    private final String value;
}
