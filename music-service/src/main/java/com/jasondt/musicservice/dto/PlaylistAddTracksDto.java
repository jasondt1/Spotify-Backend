package com.jasondt.musicservice.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class PlaylistAddTracksDto {
    private UUID trackId;
}
