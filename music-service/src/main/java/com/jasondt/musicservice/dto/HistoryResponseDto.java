package com.jasondt.musicservice.dto;

import lombok.Data;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
public class HistoryResponseDto {
    private UUID trackId;
    private String title;
    private int duration;
    private String audio;
    private List<ArtistSimpleDto> artists;
    private AlbumSimpleDto album;
    private Instant createdAt;
    private Instant updatedAt;

    private Instant playedAt;
}