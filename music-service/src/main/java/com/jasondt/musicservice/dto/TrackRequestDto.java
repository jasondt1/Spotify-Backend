package com.jasondt.musicservice.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class TrackRequestDto {
    private String title;
    private int duration;
    private String audioUrl;
    private UUID albumId;
}
