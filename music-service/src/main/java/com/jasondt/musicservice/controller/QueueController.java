package com.jasondt.musicservice.controller;

import com.jasondt.musicservice.dto.QueueItemResponseDto;
import com.jasondt.musicservice.dto.TrackResponseDto;
import com.jasondt.musicservice.service.QueueService;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/queue")
@AllArgsConstructor
@SecurityRequirement(name = "BearerAuth")
public class QueueController {

    private final QueueService service;


    @PostMapping("/tracks/{trackId}")
    public ResponseEntity<Void> addTrack(
            @Parameter(hidden = true) @RequestHeader("X-User-Id") UUID userId,
            @PathVariable UUID trackId) {
        service.addTrack(userId, trackId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


    @PostMapping("/albums/{albumId}")
    public ResponseEntity<Void> addAlbum(
            @Parameter(hidden = true) @RequestHeader("X-User-Id") UUID userId,
            @PathVariable UUID albumId) {
        service.addAlbum(userId, albumId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/playlists/{playlistId}")
    public ResponseEntity<Void> addPlaylist(
            @Parameter(hidden = true) @RequestHeader("X-User-Id") UUID userId,
            @PathVariable UUID playlistId) {
        service.addPlaylist(userId, playlistId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    public ResponseEntity<List<QueueItemResponseDto>> getQueue(
            @Parameter(hidden = true) @RequestHeader("X-User-Id") UUID userId) {
        return ResponseEntity.ok(service.getQueue(userId));
    }

    @PostMapping("/next")
    public ResponseEntity<TrackResponseDto> popNext(
            @Parameter(hidden = true) @RequestHeader("X-User-Id") UUID userId) {
        return ResponseEntity.ok(service.popNext(userId));
    }

    @DeleteMapping("/{queueItemId}")
    public ResponseEntity<Void> removeTrack(
            @Parameter(hidden = true) @RequestHeader("X-User-Id") UUID userId,
            @PathVariable UUID queueItemId) {
        service.removeTrack(userId, queueItemId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> clear(
            @Parameter(hidden = true) @RequestHeader("X-User-Id") UUID userId) {
        service.clear(userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/skip/{index}")
    public ResponseEntity<List<QueueItemResponseDto>> skip(
            @Parameter(hidden = true) @RequestHeader("X-User-Id") UUID userId,
            @PathVariable int index) {
        return ResponseEntity.ok(service.popBeforeIndex(userId, index));
    }
}
