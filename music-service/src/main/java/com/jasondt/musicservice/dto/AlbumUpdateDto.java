package com.jasondt.musicservice.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class AlbumUpdateDto {
    private String title;
    private LocalDate releaseDate;
    private UUID artistId;
    private String image;
}

