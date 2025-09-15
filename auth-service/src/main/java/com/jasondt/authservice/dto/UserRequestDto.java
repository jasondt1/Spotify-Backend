package com.jasondt.authservice.dto;


import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
public class UserRequestDto {
    private UUID userId;
    private String name;
    private Date birthday;
    private String gender;
}