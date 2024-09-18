package com.msauth.service;

import com.msauth.dto.request.LoginRequestDto;
import com.msauth.dto.request.RegisterRequestDto;
import com.msauth.dto.response.LoginResponseDto;
import com.msauth.dto.response.RegisterResponseDto;
import com.msauth.entity.Token;
import com.msauth.entity.User;
import com.msauth.exception.UserAlreadyExistsException;
import com.msauth.repository.TokenRepository;
import com.msauth.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final TokenRepository tokenRepository;


    public RegisterResponseDto register(RegisterRequestDto request) throws UserAlreadyExistsException {
        Optional<User> user1 = userRepository.findByEmail(request.getEmail());
        if (user1.isPresent())
            throw new UserAlreadyExistsException("User already exists with Email: " + request.getEmail());

        User user = new User();
        user.setEmail(request.getEmail());
        user.setUsername(request.getUserName());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setAuthorities(request.getAuthorities());

        user = userRepository.save(user);

        return new RegisterResponseDto(
                user.getId(),
                user.getEmail(),
                user.getUsername(),
                user.getAuthorities()
        );
    }


    public LoginResponseDto login(LoginRequestDto request) {

        User user = userRepository.findByUsername(request.getUserName()).orElseThrow(() -> new RuntimeException("User :" + request.getUserName() + " not Found"));


        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        saveUserToken(accessToken, refreshToken, user);

        return new LoginResponseDto(accessToken, refreshToken, "User login was successful");
    }


    private void revokeAllTokenByUser(User user) {
        List<Token> validTokens = tokenRepository.findAllAccessTokensByUser(user.getId());
        if (validTokens.isEmpty()) {
            return;
        }

        validTokens.forEach(t -> {
            t.setLoggedOut(true);
        });

        tokenRepository.saveAll(validTokens);
    }

    private void saveUserToken(String accessToken, String refreshToken, User user) {
        Token token = new Token();
        token.setAccessToken(accessToken);
        token.setRefreshToken(refreshToken);
        token.setLoggedOut(false);
        token.setUser(user);
        tokenRepository.save(token);
    }


    public ResponseEntity<LoginResponseDto> refreshToken(
            HttpServletRequest request,
            HttpServletResponse response) {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        String token = authHeader.substring(7);
        String username = jwtService.extractUsername(token);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Refresh tokenin vaxtini yoxlayiriq
        if (jwtService.isValidRefreshToken(token, user)) {
            String accessToken = jwtService.generateAccessToken(user);
            String refreshToken = jwtService.generateRefreshToken(user);

            //Kohne tokenleri legv edirik
            revokeAllTokenByUser(user);
            saveUserToken(accessToken, refreshToken, user);

            return new ResponseEntity<>(new LoginResponseDto(accessToken, refreshToken, "New token generated"), HttpStatus.OK);
        } else {
            // Refresh tokenin vaxti bitibse user tezeden login olunmalidir
            return new ResponseEntity<>(new LoginResponseDto(null, null, "Refresh token expired, please login again"), HttpStatus.UNAUTHORIZED);
        }
    }
}
