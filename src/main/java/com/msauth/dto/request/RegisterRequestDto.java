package com.msauth.dto.request;

import com.msauth.enums.Role;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class RegisterRequestDto {
    private String email;
    private String userName;
    private String password;
    private Set<Role> authorities;
}
