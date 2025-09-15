package com.jasondt.musicservice.controller;

import com.jasondt.musicservice.dto.*;
import com.jasondt.musicservice.service.PlaylistService;
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
    public ResponseEntity<PlaylistResponseDto> create(@RequestHeader("X-User-Id") UUID ownerId,
                                                      @RequestBody @Valid PlaylistCreateDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(ownerId, dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlaylistResponseDto> get(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping("/me")
    public ResponseEntity<List<PlaylistResponseDto>> mine(@RequestHeader("X-User-Id") UUID ownerId) {
        return ResponseEntity.ok(service.getMine(ownerId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PlaylistResponseDto> update(@PathVariable UUID id,
                                                      @RequestHeader("X-User-Id") UUID ownerId,
                                                      @RequestBody @Valid PlaylistUpdateDto dto) {
        return ResponseEntity.ok(service.update(id, ownerId, dto));
    }

    @PostMapping("/{id}/tracks")
    public ResponseEntity<PlaylistResponseDto> addTrack(@PathVariable UUID id,
                                                        @RequestHeader("X-User-Id") UUID ownerId,
                                                        @RequestBody @Valid PlaylistAddTracksDto dto) {
        return ResponseEntity.ok(service.addTracks(id, ownerId, dto.getTrackId()));
    }

    @DeleteMapping("/{id}/tracks/{trackId}")
    public ResponseEntity<Void> removeTrack(@PathVariable UUID id,
                                            @RequestHeader("X-User-Id") UUID ownerId,
                                            @PathVariable UUID trackId) {
        service.removeTrack(id, ownerId, trackId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id, @RequestHeader("X-User-Id") UUID ownerId) {
        service.delete(id, ownerId);
        return ResponseEntity.noContent().build();
    }
}
