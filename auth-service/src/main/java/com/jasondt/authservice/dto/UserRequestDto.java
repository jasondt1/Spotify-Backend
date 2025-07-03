package com.jasondt.authservice.dto;


import lombok.Data;

import java.util.UUID;

@Data
public class UserRequestDto {
    private UUID userId;
    private String username;
}