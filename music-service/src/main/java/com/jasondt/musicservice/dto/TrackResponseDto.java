package com.jasondt.musicservice.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class TrackResponseDto {
    private UUID id;
    private String title;
    private int duration;
    private String audioUrl;
    private AlbumResponseDto album;
}