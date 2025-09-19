package com.jasondt.musicservice.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class NowPlayingStartRequestDto {
    private UUID artistId;
    private UUID albumId;
    private UUID playlistId;
}

