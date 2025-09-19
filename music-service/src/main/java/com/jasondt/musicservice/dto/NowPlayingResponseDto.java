package com.jasondt.musicservice.dto;

import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class NowPlayingResponseDto {
    private TrackResponseDto track;
    private Instant startedAt;
    private int positionSec;
    private UUID artistId;
    private UUID albumId;
    private UUID playlistId;
}
