package com.jasondt.authservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class UserInfoDto {
    private UUID userId;
    private String role;
}