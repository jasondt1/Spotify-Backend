package com.jasondt.musicservice.dto;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class LibraryResponseDto {
    private UUID id;
    private String type;
    private String name;
    private String creator;
    private String image;
    private List<TrackResponseDto> tracks;
}
