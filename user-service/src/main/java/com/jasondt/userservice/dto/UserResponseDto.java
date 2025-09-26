package com.jasondt.userservice.dto;

import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
public class UserResponseDto {
    private UUID userId;
    private String name;
    private Date birthday;
    private String gender;
    private String profilePicture;
}
