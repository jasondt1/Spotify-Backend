package com.jasondt.musicservice.dto;

import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class GenreResponseDto {
    private UUID id;
    private String name;
    private Instant createdAt;
    private Instant updatedAt;
}
