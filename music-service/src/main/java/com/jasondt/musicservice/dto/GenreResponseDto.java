package com.jasondt.musicservice.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class GenreResponseDto {
    private UUID id;
    private String name;
}