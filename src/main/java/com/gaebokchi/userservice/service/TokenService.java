package com.gaebokchi.userservice.service;

import com.gaebokchi.userservice.dto.req.RefreshRequestDto;
import com.gaebokchi.userservice.dto.res.RefreshResponseDto;
import com.gaebokchi.userservice.entity.OAuthToken;
import com.gaebokchi.userservice.entity.User;

public interface TokenService {

    OAuthToken findByEmail(String email);

    OAuthToken saveRefreshToken(String accessToken, String refreshToken, String authentication, User user);

    RefreshResponseDto refreshToken(RefreshRequestDto refreshRequestDto);
}
