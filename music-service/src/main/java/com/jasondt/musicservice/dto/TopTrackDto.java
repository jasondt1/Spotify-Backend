package com.jasondt.musicservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TopTrackDto {
    private TrackResponseDto track;
    private long playCount;
}

