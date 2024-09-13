package com.msauth.controller;

import com.msauth.dto.request.UserRequestDto;
import com.msauth.dto.response.UserResponseDto;
import com.msauth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserResponseDto> createdUser(@RequestBody UserRequestDto userRequestDto) {
        UserResponseDto userResponseDto = userService.createUser(userRequestDto);
        return new ResponseEntity<>(userResponseDto, CREATED);
    }

    @PatchMapping
    public ResponseEntity<UserResponseDto> updatedUser(@PathVariable Long id, @RequestBody UserRequestDto userRequestDto) {
        UserResponseDto userResponseDto = userService.updateUser(id, userRequestDto);
        return new ResponseEntity<>(userResponseDto, OK);
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getAllUser() {
        List<UserResponseDto> getAllUser = userService.getAllUser();
        return new ResponseEntity<>(getAllUser, OK);
    }

    @DeleteMapping
    public void deletedUser(@PathVariable Long id) {
        userService.deletedUser(id);
    }

}
