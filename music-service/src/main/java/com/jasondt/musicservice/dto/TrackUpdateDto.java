package com.jasondt.musicservice.dto;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class TrackUpdateDto {
    private String title;
    private Integer duration;
    private String audio;
    private UUID albumId;
    private List<UUID> artistIds;
}
