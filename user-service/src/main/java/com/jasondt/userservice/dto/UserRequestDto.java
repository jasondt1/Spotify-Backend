package com.jasondt.userservice.dto;

import lombok.*;

import java.util.UUID;

@Data
public class UserRequestDto {
    private UUID id;
    private String username;
}
