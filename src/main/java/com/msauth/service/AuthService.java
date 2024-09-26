package com.msauth.service;

import com.msauth.dto.request.LoginRequestDto;
import com.msauth.dto.request.RegisterRequestDto;
import com.msauth.dto.response.AuthResponseDto;
import com.msauth.dto.response.LoginResponseDto;
import com.msauth.dto.response.RegisterResponseDto;
import com.msauth.entity.TokenEntity;
import com.msauth.entity.UserEntity;
import com.msauth.exception.UserAlreadyExistsException;
import com.msauth.repository.TokenRepository;
import com.msauth.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.msauth.mapper.TokenMapper.buildTokenEntity;
import static com.msauth.mapper.UserMapper.USER_MAPPER;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final TokenRepository tokenRepository;

    public RegisterResponseDto register(RegisterRequestDto request) throws UserAlreadyExistsException {
        var user1 = userRepository.findByEmail(request.getEmail());
        if (user1.isPresent())
            throw new UserAlreadyExistsException("User already exists with Email: " + request.getEmail());

        var user = USER_MAPPER.buildUserEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user = userRepository.save(user);

        return USER_MAPPER.buildRegisterResponseDto(user);
    }

    public LoginResponseDto login(LoginRequestDto request) {
        var user = userRepository.findByUsername(request.getUserName())
                .orElseThrow(() -> new RuntimeException("User :" + request.getUserName() + " not Found"));

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        saveUserToken(accessToken, refreshToken, user);

        return new LoginResponseDto(accessToken, refreshToken, "User login was successful");
    }

    private void revokeAllTokenByUser(UserEntity user) {
        List<TokenEntity> validTokens = tokenRepository.findAllAccessTokensByUser(user.getId());
        if (validTokens.isEmpty()) return;
        validTokens.forEach(t -> t.setLoggedOut(true));

        tokenRepository.saveAll(validTokens);
    }

    private void saveUserToken(String accessToken, String refreshToken, UserEntity user) {
        var token = buildTokenEntity(accessToken, refreshToken, user);
        tokenRepository.save(token);
    }

    public ResponseEntity<LoginResponseDto> refreshToken(
            HttpServletRequest request,
            HttpServletResponse response) {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return new ResponseEntity<>(UNAUTHORIZED);
        }

        String token = authHeader.substring(7);
        String username = jwtService.extractUsername(token);

        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Refresh tokenin vaxtini yoxlayiriq
        if (jwtService.isValidRefreshToken(token, user)) {
            String accessToken = jwtService.generateAccessToken(user);
            String refreshToken = jwtService.generateRefreshToken(user);

            //Kohne tokenleri legv edirik
            revokeAllTokenByUser(user);
            saveUserToken(accessToken, refreshToken, user);

            return ResponseEntity.status(OK).body(new LoginResponseDto(
                    accessToken, refreshToken, "New token generated"));
        } else {
            // Refresh tokenin vaxti bitibse user tezeden login olunmalidir
            return ResponseEntity.status(UNAUTHORIZED).body(new LoginResponseDto
                    (null, null, "Refresh token expired, please login again"));
        }
    }

    public AuthResponseDto getUserById(Long id) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found By " + id + "id"));

        return new AuthResponseDto(user.getId());
    }


}
