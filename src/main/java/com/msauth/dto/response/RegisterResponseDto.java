package com.msauth.dto.response;

import com.msauth.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
public class RegisterResponseDto {
    private Long id;
    private String email;
    private String userName;
    private Set<Role> authorities;
}
