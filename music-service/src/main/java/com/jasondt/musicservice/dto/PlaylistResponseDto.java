package com.jasondt.musicservice.dto;

import lombok.Data;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
public class PlaylistResponseDto {
    private UUID id;
    private UUID ownerId;
    private String name;
    private String description;
    private String image;
    private List<TrackResponseDto> tracks;
    private Instant createdAt;
    private Instant updatedAt;
    private UserResponseDto owner;
}

