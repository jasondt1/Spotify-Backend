package com.jasondt.musicservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TopArtistDto {
    private ArtistSimpleDto artist;
    private long playCount;
}
