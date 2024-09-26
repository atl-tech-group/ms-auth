package com.msauth.controller;

import com.msauth.dto.request.LoginRequestDto;
import com.msauth.dto.request.RegisterRequestDto;
import com.msauth.dto.response.AuthResponseDto;
import com.msauth.dto.response.LoginResponseDto;
import com.msauth.dto.response.RegisterResponseDto;
import com.msauth.exception.UserAlreadyExistsException;
import com.msauth.service.AuthService;
import com.msauth.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class AuthController {


    private final AuthService authService;
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponseDto> register(@RequestBody RegisterRequestDto request) throws UserAlreadyExistsException {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto loginRequest) {
        return ResponseEntity.ok(authService.login(loginRequest));
    }

    @PostMapping("/refresh_token")
    public ResponseEntity<LoginResponseDto> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        return authService.refreshToken(request, response);
    }

    @GetMapping("{id}")
    public ResponseEntity<AuthResponseDto> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(authService.getUserById(id));
    }

}
