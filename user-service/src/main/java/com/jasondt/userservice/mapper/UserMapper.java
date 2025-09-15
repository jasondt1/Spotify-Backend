package com.jasondt.userservice.mapper;

import com.jasondt.userservice.dto.UserRequestDto;
import com.jasondt.userservice.dto.UserResponseDto;
import com.jasondt.userservice.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(source = "id", target = "userId")
    UserResponseDto toDto(User user);

    @Mapping(source = "userId", target = "id")
    User toEntity(UserRequestDto userDto);

    List<UserResponseDto> toDto(List<User> users);
}
