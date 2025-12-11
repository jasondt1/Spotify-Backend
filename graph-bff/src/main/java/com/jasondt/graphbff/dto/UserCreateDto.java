package com.jasondt.graphbff.dto;

import lombok.Data;
import java.util.UUID;
import java.util.Date;

@Data
public class UserCreateDto {
    private UUID userId;
    private String name;
    private Date birthday;
    private String gender;
}
