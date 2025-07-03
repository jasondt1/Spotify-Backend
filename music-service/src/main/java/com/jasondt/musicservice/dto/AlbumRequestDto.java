package com.jasondt.musicservice.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class AlbumRequestDto {
    private String title;
    private UUID artistId;
}
