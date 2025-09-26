package com.jasondt.musicservice.controller;

import com.jasondt.musicservice.dto.*;
import com.jasondt.musicservice.service.HistoryService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/history")
@AllArgsConstructor
@SecurityRequirement(name = "BearerAuth")
public class HistoryController {

    private final HistoryService service;

    @GetMapping("/tracks/{trackId}/plays")
    public ResponseEntity<PlayCountResponseDto> getTrackPlays(@PathVariable UUID trackId) {
        return ResponseEntity.ok(service.getTrackPlayCount(trackId));
    }

    @GetMapping("/me")
    public ResponseEntity<List<HistoryResponseDto>> myHistory(
            @Parameter(hidden = true) @RequestHeader("X-User-Id") UUID userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        return ResponseEntity.ok(service.getUserHistory(userId, page, size));
    }
    @GetMapping("/users/{userId}/top-tracks")
    public ResponseEntity<List<TopTrackDto>> userTopTracks(@PathVariable UUID userId) {
        return ResponseEntity.ok(service.getUserTopTracksLast30(userId));
    }

    @GetMapping("/users/{userId}/top-artists")
    public ResponseEntity<List<TopArtistDto>> userTopArtists(@PathVariable UUID userId) {
        return ResponseEntity.ok(service.getUserTopArtistsLast30(userId, false));
    }

    @GetMapping("/top-tracks")
    public ResponseEntity<List<TopTrackDto>> getTopTracksAllTime(
            @RequestParam(defaultValue = "20") int limit) {
        return ResponseEntity.ok(service.getTopTracksAllTime(limit));
    }

    @GetMapping("/top-artists")
    public ResponseEntity<List<TopArtistDto>> getTopArtistsAllTime(
            @RequestParam(defaultValue = "20") int limit) {
        return ResponseEntity.ok(service.getTopArtistsAllTime(limit));
    }

    @GetMapping("/top-albums")
    public ResponseEntity<List<AlbumResponseDto>> getAlbumsWithTopTracksAllTime(
            @RequestParam(defaultValue = "20") int limit) {
        return ResponseEntity.ok(service.getAlbumsWithTopTracksAllTime(limit));
    }

}
