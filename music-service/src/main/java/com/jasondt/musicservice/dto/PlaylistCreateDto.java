package com.jasondt.musicservice.dto;

import lombok.Data;

import java.util.List;

@Data
public class PlaylistCreateDto {
    private String name;
    private String description;
    private String image;
    private List<java.util.UUID> trackIds;
}

