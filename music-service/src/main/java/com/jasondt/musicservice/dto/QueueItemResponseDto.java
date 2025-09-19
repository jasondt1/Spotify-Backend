package com.jasondt.musicservice.dto;

import java.util.UUID;
import lombok.Data;

@Data
public class QueueItemResponseDto {
    private UUID id;
    private TrackResponseDto track;
}
