package com.jasondt.userservice.service;

import com.jasondt.userservice.dto.UserCreateDto;
import com.jasondt.userservice.dto.UserResponseDto;
import com.jasondt.userservice.dto.UserUpdateDto;
import com.jasondt.userservice.exception.DatabaseException;
import com.jasondt.userservice.mapper.UserMapper;
import com.jasondt.userservice.model.User;
import com.jasondt.userservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    private User user;
    private UserResponseDto userResponseDto;
    private UUID userId;

    private Date convert(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        user = new User();
        user.setId(userId);
        user.setName("John Doe");
        user.setBirthday(convert(LocalDate.of(1990, 1, 1)));
        user.setGender("Male");
        user.setDeleted(false);

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
        createDto.setBirthday(convert(LocalDate.of(1990, 1, 1)));
        createDto.setGender("Male");

        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toDto(any(User.class))).thenReturn(userResponseDto);

        UserResponseDto result = userService.createUser(createDto);

        assertNotNull(result);
        assertEquals(userResponseDto.getName(), result.getName());
        verify(userRepository).save(any(User.class));
        verify(userMapper).toDto(any(User.class));
    }

    @Test
    void createUser_DataIntegrityViolation() {
        UserCreateDto createDto = new UserCreateDto();
        when(userRepository.save(any(User.class))).thenThrow(new DataIntegrityViolationException("Duplicate entry"));

        assertThrows(DatabaseException.class, () -> userService.createUser(createDto));
    }

    @Test
    void getAllUsers_Success() {
        List<User> users = Arrays.asList(user);
        List<UserResponseDto> dtos = Arrays.asList(userResponseDto);

        when(userRepository.findAllByDeletedFalse()).thenReturn(users);
        when(userMapper.toDto(users)).thenReturn(dtos);

        List<UserResponseDto> result = userService.getAllUsers();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(userRepository).findAllByDeletedFalse();
    }

    @Test
    void getAllUsers_Exception() {
        when(userRepository.findAllByDeletedFalse()).thenThrow(new RuntimeException("DB Error"));
        assertThrows(DatabaseException.class, () -> userService.getAllUsers());
    }

    @Test
    void getUserById_Success() {
        when(userRepository.findByIdAndDeletedFalse(userId)).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(userResponseDto);

        Optional<UserResponseDto> result = userService.getUserById(userId);

        assertTrue(result.isPresent());
        assertEquals(userResponseDto.getName(), result.get().getName());
    }

    @Test
    void getUserById_NotFound() {
        when(userRepository.findByIdAndDeletedFalse(userId)).thenReturn(Optional.empty());

        Optional<UserResponseDto> result = userService.getUserById(userId);

        assertTrue(result.isEmpty());
    }

    @Test
    void getUserById_Exception() {
        when(userRepository.findByIdAndDeletedFalse(userId)).thenThrow(new RuntimeException("DB Error"));
        assertThrows(DatabaseException.class, () -> userService.getUserById(userId));
    }

    @Test
    void updateUser_Success() {
        UserUpdateDto updateDto = new UserUpdateDto();
        updateDto.setName("Jane Doe");

        User updatedUser = new User();
        updatedUser.setId(userId);
        updatedUser.setName("Jane Doe");

        UserResponseDto updatedDto = new UserResponseDto();
        updatedDto.setName("Jane Doe");

        when(userRepository.findByIdAndDeletedFalse(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);
        when(userMapper.toDto(any(User.class))).thenReturn(updatedDto);

        Optional<UserResponseDto> result = userService.updateUser(userId, updateDto);

        assertTrue(result.isPresent());
        assertEquals("Jane Doe", result.get().getName());
        verify(userRepository).save(user);
    }

    @Test
    void updateUser_NotFound() {
        UserUpdateDto updateDto = new UserUpdateDto();
        when(userRepository.findByIdAndDeletedFalse(userId)).thenReturn(Optional.empty());

        Optional<UserResponseDto> result = userService.updateUser(userId, updateDto);

        assertTrue(result.isEmpty());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateUser_Exception() {
        UserUpdateDto updateDto = new UserUpdateDto();
        when(userRepository.findByIdAndDeletedFalse(userId)).thenThrow(new RuntimeException("DB Error"));
        assertThrows(DatabaseException.class, () -> userService.updateUser(userId, updateDto));
    }

    @Test
    void deleteUser_Success() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        boolean result = userService.deleteUser(userId);

        assertTrue(result);
        assertTrue(user.isDeleted());
        verify(userRepository).save(user);
    }

    @Test
    void deleteUser_NotFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        boolean result = userService.deleteUser(userId);

        assertFalse(result);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void deleteUser_AlreadyDeleted() {
        user.setDeleted(true);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        boolean result = userService.deleteUser(userId);

        assertFalse(result);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void deleteUser_Exception() {
        when(userRepository.findById(userId)).thenThrow(new RuntimeException("DB Error"));
        assertThrows(DatabaseException.class, () -> userService.deleteUser(userId));
    }
}
