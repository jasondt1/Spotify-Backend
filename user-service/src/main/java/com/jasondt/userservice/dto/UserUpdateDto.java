package com.jasondt.userservice.dto;

import lombok.Data;

import java.util.Date;

@Data
public class UserUpdateDto {
    private String name;
    private Date birthday;
    private String gender;
    private String profilePicture;
}

