package com.gaebokchi.userservice.dto.req;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.validation.constraints.NotEmpty;

@ToString
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshRequestDto {

    @NotEmpty(message = "email을 입력해주세요.")
    private String email;

    @NotEmpty(message = "access token이 없습니다.")
    @JsonProperty("access_token")
    private String accessToken;

    @NotEmpty(message = "refresh token이 없습니다.")
    @JsonProperty("refresh_token")
    private String refreshToken;
}
