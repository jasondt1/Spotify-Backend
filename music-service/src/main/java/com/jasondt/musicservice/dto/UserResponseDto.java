package com.jasondt.musicservice.dto;

import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
public class UserResponseDto {
    private UUID id;
    private String name;
    private String email;
    private String gender;
    private Date birthday;
}
