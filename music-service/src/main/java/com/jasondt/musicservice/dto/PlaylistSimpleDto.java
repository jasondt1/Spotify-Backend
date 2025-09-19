package com.jasondt.musicservice.dto;

import lombok.Data;

import java.util.UUID;
import java.util.List;

@Data
public class PlaylistSimpleDto {
    private UUID id;
    private UUID ownerId;
    private String name;
    private String image;
    private UserResponseDto owner;
    private List<TrackResponseDto> tracks;
}
