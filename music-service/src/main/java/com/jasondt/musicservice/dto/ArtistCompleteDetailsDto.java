package com.jasondt.musicservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArtistCompleteDetailsDto {
    private ArtistResponseDto artist;
    private List<TopTrackDto> topTracks;
    private Long monthlyListeners;
}
