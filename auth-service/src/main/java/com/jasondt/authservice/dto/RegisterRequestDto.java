package com.jasondt.authservice.dto;

import lombok.Data;

import java.util.Date;

@Data
public class RegisterRequestDto {
    private String email;
    private String password;
    private String name;
    private Date birthday;
    private String gender;
}