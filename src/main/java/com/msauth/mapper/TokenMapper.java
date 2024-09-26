package com.msauth.mapper;

import com.msauth.entity.TokenEntity;
import com.msauth.entity.UserEntity;
import com.msauth.enums.TokenType;

public class TokenMapper {
    public static TokenEntity buildTokenEntity(String accessToken, String refreshToken, UserEntity user) {
        return TokenEntity.builder()
                .user(user)
                .loggedOut(false)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType(TokenType.BEARER)
                .build();
    }
}
