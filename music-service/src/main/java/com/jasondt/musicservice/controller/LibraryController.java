package com.jasondt.musicservice.controller;

import com.jasondt.musicservice.dto.LibraryResponseDto;
import com.jasondt.musicservice.service.LibraryService;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/library")
public class LibraryController {

    private final LibraryService libraryService;

    public LibraryController(LibraryService libraryService) {
        this.libraryService = libraryService;
    }

    @GetMapping("/me")
    public ResponseEntity<List<LibraryResponseDto>> me(
            @Parameter(hidden = true) @RequestHeader("X-User-Id") UUID userId) {
        return ResponseEntity.ok(libraryService.getLibrary(userId));
    }

    @PostMapping("/playlists/{playlistId}")
    public ResponseEntity<List<LibraryResponseDto>> addPlaylist(
            @Parameter(hidden = true) @RequestHeader("X-User-Id") UUID userId,
            @PathVariable UUID playlistId) {
        return ResponseEntity.ok(libraryService.addPlaylist(userId, playlistId));
    }

    @DeleteMapping("/playlists/{playlistId}")
    public ResponseEntity<List<LibraryResponseDto>> removePlaylist(
            @Parameter(hidden = true) @RequestHeader("X-User-Id") UUID userId,
            @PathVariable UUID playlistId) {
        return ResponseEntity.ok(libraryService.removePlaylist(userId, playlistId));
    }

    @PostMapping("/albums/{albumId}")
    public ResponseEntity<List<LibraryResponseDto>> addAlbum(
            @Parameter(hidden = true) @RequestHeader("X-User-Id") UUID userId,
            @PathVariable UUID albumId) {
        return ResponseEntity.ok(libraryService.addAlbum(userId, albumId));
    }

    @DeleteMapping("/albums/{albumId}")
    public ResponseEntity<List<LibraryResponseDto>> removeAlbum(
            @Parameter(hidden = true) @RequestHeader("X-User-Id") UUID userId,
            @PathVariable UUID albumId) {
        return ResponseEntity.ok(libraryService.removeAlbum(userId, albumId));
    }
}
