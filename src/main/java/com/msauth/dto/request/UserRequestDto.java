package com.msauth.dto.request;

import com.msauth.enums.Role;
import lombok.*;

import java.util.Set;

@Getter
@Setter
public class UserRequestDto {

    private String name;
    private String userName;
    private String password;
    private Set<Role> authorities;
}
