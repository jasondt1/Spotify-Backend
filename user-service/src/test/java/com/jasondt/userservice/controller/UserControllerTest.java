package com.jasondt.userservice.controller;

import com.jasondt.userservice.dto.UserCreateDto;
import com.jasondt.userservice.dto.UserResponseDto;
import com.jasondt.userservice.dto.UserUpdateDto;
import com.jasondt.userservice.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private UserResponseDto userResponseDto;
    private UUID userId;

    private Date convert(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        userResponseDto = new UserResponseDto();
        userResponseDto.setUserId(userId);
        userResponseDto.setName("John Doe");
        userResponseDto.setBirthday(convert(LocalDate.of(1990, 1, 1)));
        userResponseDto.setGender("Male");
    }


    @Test
    void createUser_Success() {
        UserCreateDto createDto = new UserCreateDto();
        createDto.setName("John Doe");

        when(userService.createUser(any(UserCreateDto.class))).thenReturn(userResponseDto);

        ResponseEntity<UserResponseDto> response = userController.createUser(createDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(userResponseDto, response.getBody());
        verify(userService).createUser(createDto);
    }

    @Test
    void getUsers_Success() {
        List<UserResponseDto> list = Arrays.asList(userResponseDto);
        when(userService.getAllUsers()).thenReturn(list);

        ResponseEntity<List<UserResponseDto>> response = userController.getUsers();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(userService).getAllUsers();
    }

    @Test
    void getUserById_Success() {
        when(userService.getUserById(userId)).thenReturn(userResponseDto);

        ResponseEntity<UserResponseDto> response = userController.getUserById(userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userResponseDto, response.getBody());
    }

    @Test
    void getUserById_NotFound() {
        when(userService.getUserById(userId)).thenReturn(null);

        ResponseEntity<UserResponseDto> response = userController.getUserById(userId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void getMe_Success() {
        when(userService.getUserById(userId)).thenReturn(userResponseDto);

        ResponseEntity<UserResponseDto> response = userController.getMe(userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userResponseDto, response.getBody());
    }

    @Test
    void getMe_NotFound() {
        when(userService.getUserById(userId)).thenReturn(null);

        ResponseEntity<UserResponseDto> response = userController.getMe(userId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void deleteUser_Success() {
        when(userService.deleteUser(userId)).thenReturn(true);

        ResponseEntity<Void> response = userController.deleteUser(userId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void deleteUser_NotFound() {
        when(userService.deleteUser(userId)).thenReturn(false);

        ResponseEntity<Void> response = userController.deleteUser(userId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void updateUser_Success() {
        UserUpdateDto updateDto = new UserUpdateDto();
        when(userService.updateUser(eq(userId), any(UserUpdateDto.class))).thenReturn(userResponseDto);

        ResponseEntity<UserResponseDto> response = userController.updateUser(userId, updateDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userResponseDto, response.getBody());
    }

    @Test
    void updateUser_NotFound() {
        UserUpdateDto updateDto = new UserUpdateDto();
        when(userService.updateUser(eq(userId), any(UserUpdateDto.class))).thenReturn(null);

        ResponseEntity<UserResponseDto> response = userController.updateUser(userId, updateDto);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
