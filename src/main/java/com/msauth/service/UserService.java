package com.msauth.service;

import com.msauth.dto.request.UserRequestDto;
import com.msauth.dto.response.UserResponseDto;
import com.msauth.entity.User;
import com.msauth.exception.UserNotFoundException;
import com.msauth.mapper.UserMapper;
import com.msauth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public Optional<User> getByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public UserResponseDto createUser(UserRequestDto userRequestDto) {
        User user = new User();
        user.setName(userRequestDto.getName());
        user.setUsername(userRequestDto.getUserName());
        user.setPassword(passwordEncoder.encode(userRequestDto.getPassword()));
        user.setAuthorities(userRequestDto.getAuthorities());
        user.setAccountNonExpired(true);
        user.setAccountNonLocked(true);
        user.setEnabled(true);
        user.setCredentialsNonExpired(true);
        User savedUser = userRepository.save(user);

         return UserMapper.INSTANCE.userToResponseDto(savedUser);
    }

    public UserResponseDto updateUser(Long id, UserRequestDto userRequestDto) {
        User user = userRepository.findById(id)
                .orElseThrow(()-> new UserNotFoundException("User Id " + id + "not found"));

        user.setName(userRequestDto.getName());
        user.setUsername(user.getUsername());

        User updatedUser = userRepository.save(user);

        return UserMapper.INSTANCE.userToResponseDto(updatedUser);
    }

    public List<UserResponseDto> getAllUser() {
        return userRepository.findAll()
                .stream()
                .map(UserMapper.INSTANCE :: userToResponseDto)
                .collect(Collectors.toList());
    }

    public void deletedUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(()-> new UserNotFoundException("User Id " + id + "not found"));

        userRepository.delete(user);
    }

}
