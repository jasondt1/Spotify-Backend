package com.jasondt.musicservice.controller;

import com.jasondt.musicservice.dto.NowPlayingResponseDto;
import com.jasondt.musicservice.dto.TrackResponseDto;
import com.jasondt.musicservice.dto.NowPlayingStartRequestDto;
import com.jasondt.musicservice.service.HistoryService;
import com.jasondt.musicservice.service.NowPlayingService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/now-playing")
@AllArgsConstructor
@SecurityRequirement(name = "BearerAuth")
public class NowPlayingController {

    private final NowPlayingService nowPlayingService;
    private final HistoryService historyService;

    @GetMapping("/me")
    public ResponseEntity<NowPlayingResponseDto> me(
            @Parameter(hidden = true) @RequestHeader("X-User-Id") UUID userId) {
        return nowPlayingService.getNowPlaying(userId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NO_CONTENT).build());
    }

    @PostMapping("/tracks/{trackId}")
    public ResponseEntity<NowPlayingResponseDto> setTrack(
            @Parameter(hidden = true) @RequestHeader("X-User-Id") UUID userId,
            @PathVariable UUID trackId,
            @RequestBody(required = false) NowPlayingStartRequestDto source) {
        UUID artistId = source == null ? null : source.getArtistId();
        UUID albumId = source == null ? null : source.getAlbumId();
        UUID playlistId = source == null ? null : source.getPlaylistId();
        NowPlayingResponseDto np = nowPlayingService.setNowPlaying(userId, trackId, artistId, albumId, playlistId);
        historyService.recordPlay(userId, trackId);
        return ResponseEntity.status(HttpStatus.CREATED).body(np);
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> stop(
            @Parameter(hidden = true) @RequestHeader("X-User-Id") UUID userId) {
        nowPlayingService.clear(userId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/me/position")
    public ResponseEntity<NowPlayingResponseDto> updatePosition(
            @Parameter(hidden = true) @RequestHeader("X-User-Id") UUID userId,
            @RequestParam(name = "sec") int positionSec) {
        return ResponseEntity.ok(nowPlayingService.updatePosition(userId, positionSec));
    }
}
