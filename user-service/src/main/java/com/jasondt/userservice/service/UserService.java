package com.jasondt.userservice.service;

import com.jasondt.userservice.dto.UserRequestDto;
import com.jasondt.userservice.dto.UserCreateDto;
import com.jasondt.userservice.dto.UserUpdateDto;
import com.jasondt.userservice.dto.UserResponseDto;
import com.jasondt.userservice.exception.DatabaseException;
import com.jasondt.userservice.mapper.UserMapper;
import com.jasondt.userservice.model.User;
import com.jasondt.userservice.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserResponseDto createUser(UserCreateDto userDto) {
        try {
            User user = new User();
            user.setId(UUID.randomUUID());
            user.setName(userDto.getName());
            user.setBirthday(userDto.getBirthday());
            user.setGender(userDto.getGender());
            User savedUser = userRepository.save(user);
            return userMapper.toDto(savedUser);

        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Failed to save user: " + e.getMessage(), e);
        }
    }

    public List<UserResponseDto> getAllUsers() {
        try {
            List<User> users = userRepository.findAllByDeletedFalse();
            return userMapper.toDto(users);
        } catch (Exception e) {
            throw new DatabaseException("Failed to fetch users: " + e.getMessage(), e);
        }
    }

    public Optional<UserResponseDto> getUserById(UUID id) {
        try {
            return userRepository.findByIdAndDeletedFalse(id).map(userMapper::toDto);
        } catch (Exception e) {
            throw new DatabaseException("Failed to fetch user: " + e.getMessage(), e);
        }
    }

    public Optional<UserResponseDto> updateUser(UUID id, UserUpdateDto dto) {
        try {
            return userRepository.findByIdAndDeletedFalse(id).map(user -> {
                if (dto.getName() != null) user.setName(dto.getName());
                if (dto.getBirthday() != null) user.setBirthday(dto.getBirthday());
                if (dto.getGender() != null) user.setGender(dto.getGender());
                if (dto.getProfilePicture() != null) user.setProfilePicture(dto.getProfilePicture());

                return userMapper.toDto(userRepository.save(user));
            });
        } catch (Exception e) {
            throw new DatabaseException("Failed to update user: " + e.getMessage(), e);
        }
    }


    public boolean deleteUser(UUID id) {
        try {
            Optional<User> optionalUser = userRepository.findById(id);
            if (optionalUser.isEmpty()) {
                return false;
            }
            User user = optionalUser.get();
            if (user.isDeleted()) {
                return false;
            }
            user.setDeleted(true);
            userRepository.save(user);
            return true;
        } catch (Exception e) {
            throw new DatabaseException("Failed to delete user: " + e.getMessage(), e);
        }
    }
}
