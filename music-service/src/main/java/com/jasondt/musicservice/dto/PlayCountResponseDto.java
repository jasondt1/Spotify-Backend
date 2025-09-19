package com.jasondt.musicservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlayCountResponseDto {
    private UUID trackId;
    private long totalPlays;
    private Long myPlays;
}

