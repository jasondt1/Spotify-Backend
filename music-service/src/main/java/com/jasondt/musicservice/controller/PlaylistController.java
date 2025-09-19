package com.jasondt.musicservice.controller;

import com.jasondt.musicservice.dto.*;
import com.jasondt.musicservice.service.PlaylistService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/playlists")
@AllArgsConstructor
@SecurityRequirement(name = "BearerAuth")
public class PlaylistController {

    private final PlaylistService service;

    @PostMapping
    public ResponseEntity<PlaylistResponseDto> create(
            @Parameter(hidden = true) @RequestHeader("X-User-Id") UUID userId,
            @RequestBody @Valid PlaylistCreateDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(userId, dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlaylistResponseDto> get(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping("/me")
    public ResponseEntity<List<PlaylistResponseDto>> mine(
            @Parameter(hidden = true) @RequestHeader("X-User-Id") UUID userId) {
        return ResponseEntity.ok(service.getMine(userId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PlaylistResponseDto> update(
            @PathVariable UUID id,
            @Parameter(hidden = true) @RequestHeader("X-User-Id") UUID userId,
            @RequestBody @Valid PlaylistUpdateDto dto) {
        return ResponseEntity.ok(service.update(id, userId, dto));
    }

    @PostMapping("/{id}/tracks/{trackId}")
    public ResponseEntity<PlaylistResponseDto> addTrack(
            @PathVariable UUID id,
            @PathVariable UUID trackId,
            @Parameter(hidden = true) @RequestHeader("X-User-Id") UUID userId) {
        return ResponseEntity.ok(service.addTracks(id, userId, trackId));
    }

    @DeleteMapping("/{id}/tracks/{trackId}")
    public ResponseEntity<Void> removeTrack(
            @PathVariable UUID id,
            @Parameter(hidden = true) @RequestHeader("X-User-Id") UUID userId,
            @PathVariable UUID trackId) {
        service.removeTrack(id, userId, trackId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID id,
            @Parameter(hidden = true) @RequestHeader("X-User-Id") UUID userId) {
        service.delete(id, userId);
        return ResponseEntity.noContent().build();
    }
}
