package com.jasondt.userservice.mapper;

import com.jasondt.userservice.dto.UserRequestDto;
import com.jasondt.userservice.dto.UserResponseDto;
import com.jasondt.userservice.model.User;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponseDto toDto(User user);
    User toEntity(UserRequestDto userDto);
    List<UserResponseDto> toDto(List<User> users);
}