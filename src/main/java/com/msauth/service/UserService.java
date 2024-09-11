package com.msauth.service;

import com.msauth.dto.request.UserRequestDto;
import com.msauth.dto.response.UserResponseDto;
import com.msauth.entity.User;
import com.msauth.exception.UserNotFoundException;
import com.msauth.mapper.UserMapper;
import com.msauth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public UserResponseDto createUser(UserRequestDto userRequestDto) {
        User user = UserMapper.INSTANCE.requestDtoToUser(userRequestDto);
        User savedUser = userRepository.save(user);

         return UserMapper.INSTANCE.userToResponseDto(savedUser);
    }

    public UserResponseDto updateUser(Long id, UserRequestDto userRequestDto) {
        User user = userRepository.findById(id)
                .orElseThrow(()-> new UserNotFoundException("User Id " + id + "not found"));

        user.setFirstName(userRequestDto.getFirstName());
        user.setLastName(user.getLastName());

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
