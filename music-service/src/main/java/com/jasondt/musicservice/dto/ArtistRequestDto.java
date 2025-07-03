package com.jasondt.musicservice.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class ArtistRequestDto {
    private String name;
    private UUID genreId;
}
