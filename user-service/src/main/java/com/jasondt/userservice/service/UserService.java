package com.jasondt.userservice.service;

import com.jasondt.userservice.dto.UserRequestDto;
import com.jasondt.userservice.dto.UserResponseDto;
import com.jasondt.userservice.exception.DatabaseException;
import com.jasondt.userservice.exception.UsernameAlreadyExistsException;
import com.jasondt.userservice.mapper.UserMapper;
import com.jasondt.userservice.model.User;
import com.jasondt.userservice.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserResponseDto createUser(UserRequestDto userDto) {
        try {
            if (userRepository.findByUsername(userDto.getUsername()).isPresent()) {
                throw new UsernameAlreadyExistsException("Username already taken");
            }

            User user = userMapper.toEntity(userDto);
            User savedUser = userRepository.save(user);
            return userMapper.toDto(savedUser);

        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Failed to save user: " + e.getMessage(), e);
        }
    }

    public List<UserResponseDto> getAllUsers() {
        try {
            List<User> users = userRepository.findAll();
            return userMapper.toDto(users);
        } catch (Exception e) {
            throw new DatabaseException("Failed to fetch users: " + e.getMessage(), e);
        }
    }
}

