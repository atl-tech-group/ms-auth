package com.msauth.service;

import com.msauth.config.properties.UserCreatedTopicProperties;
import com.msauth.dto.request.LoginRequestDto;
import com.msauth.dto.request.RegisterRequestDto;
import com.msauth.dto.response.AuthResponseDto;
import com.msauth.dto.response.LoginResponseDto;
import com.msauth.dto.response.RegisterResponseDto;
import com.msauth.entity.TokenEntity;
import com.msauth.entity.UserEntity;
import com.msauth.events.UserCreatedEvent;
import com.msauth.exception.UserAlreadyExistsException;
import com.msauth.producer.KafkaProducer;
import com.msauth.repository.TokenRepository;
import com.msauth.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.msauth.enums.ErrorMessage.*;
import static com.msauth.mapper.TokenMapper.buildTokenEntity;
import static com.msauth.mapper.UserMapper.USER_MAPPER;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.kafka.support.KafkaHeaders.KEY;
import static org.springframework.kafka.support.KafkaHeaders.TOPIC;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final TokenRepository tokenRepository;
    private final AuthenticationManager authenticationManager;
    private final KafkaProducer kafkaProducer;
    private final UserCreatedTopicProperties userCreatedTopicProperties;


    @CachePut(cacheNames = "user", key = " 'registerUser' + #result.id", unless = "#result == null")
    public RegisterResponseDto register(RegisterRequestDto request) throws UserAlreadyExistsException {
        var user1 = userRepository.findByEmail(request.getEmail());
        if (user1.isPresent())
            throw new UserAlreadyExistsException("User already exists with Email: " + request.getEmail());

        var user = USER_MAPPER.buildUserEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user = userRepository.save(user);

        sendUserCreatedEvent(user);
        return USER_MAPPER.buildRegisterResponseDto(user);
    }

    public LoginResponseDto login(LoginRequestDto request) {

        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        if (authentication.isAuthenticated()) {
            var user = (UserEntity) authentication.getPrincipal();
            String accessToken = jwtService.generateAccessToken(user);
            String refreshToken = jwtService.generateRefreshToken(user);
            saveUserToken(accessToken, refreshToken, user);

            return new LoginResponseDto(accessToken, refreshToken, "User login was successful");
        }

        throw new UsernameNotFoundException(USER_NOT_FOUND.format(request.getUsername()));
    }

    public ResponseEntity<LoginResponseDto> refreshToken(
            HttpServletRequest request,
            HttpServletResponse response) {
        String authHeader = request.getHeader(AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(UNAUTHORIZED).build();
        }

        String token = authHeader.substring(7);
        String username = jwtService.extractUsername(token);

        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(USER_NOT_FOUND.getMessage()));

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
                    (null, null, EXPIRED_TOKEN.getMessage()));
        }
    }

    @Cacheable(cacheNames = "user", key = "#id")
    public AuthResponseDto getUserById(Long id) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND_BY_ID.format(id)));

        return AuthResponseDto.builder().id(user.getId()).build();
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

    private void sendUserCreatedEvent(UserEntity user) {
        UserCreatedEvent event = UserCreatedEvent.builder()
                .id(user.getId())
                .email(user.getEmail())
                .dateOfBirth(user.getDateOfBirth())
                .status(true)
                .build();

        Map<String, Object> headers = new HashMap<>();
        headers.put(TOPIC, userCreatedTopicProperties.getTopicName());
        headers.put(KEY, user.getId().toString());

        kafkaProducer.sendMessage(new GenericMessage<>(event, headers));
    }
}
