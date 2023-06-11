package com.gaebokchi.userservice.dto.res;

import lombok.*;

@ToString
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshResponseDto {
    private String email;
    private String accessToken;
    private String refreshToken;
}
