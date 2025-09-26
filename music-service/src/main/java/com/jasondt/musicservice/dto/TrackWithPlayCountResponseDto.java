package com.jasondt.musicservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrackWithPlayCountResponseDto {
    TrackResponseDto track;
    long playCount;
}
