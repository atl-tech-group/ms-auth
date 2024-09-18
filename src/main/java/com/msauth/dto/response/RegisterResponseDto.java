package com.msauth.dto.response;

import com.msauth.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;

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
