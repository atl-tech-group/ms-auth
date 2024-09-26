package com.msauth.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorMessage {
    USER_NOT_FOUND("User not found"),

    USER_NOT_FOUND_BY_ID("User not found with id: %d"),

    EXPIRED_TOKEN("Refresh token expired, please login again");

    private final String message;
    public String format(Object... args) {
        return String.format(message, args);
    }
}
