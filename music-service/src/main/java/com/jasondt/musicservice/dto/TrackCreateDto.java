package com.jasondt.musicservice.dto;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class TrackCreateDto {
    private String title;
    private int duration;
    private String audio;
    private UUID albumId;
    private List<UUID> artistIds;
}
