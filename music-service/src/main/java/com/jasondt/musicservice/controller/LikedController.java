package com.jasondt.musicservice.controller;

import com.jasondt.musicservice.dto.TrackResponseDto;
import com.jasondt.musicservice.service.LikedService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/likes")
@AllArgsConstructor
@SecurityRequirement(name = "BearerAuth")
public class LikedController {

    private final LikedService service;

    @PostMapping("/tracks/{trackId}")
    public ResponseEntity<Void> like(@RequestHeader("X-User-Id") UUID userId,
                                     @PathVariable UUID trackId) {
        service.like(userId, trackId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/tracks/{trackId}")
    public ResponseEntity<Void> unlike(@RequestHeader("X-User-Id") UUID userId,
                                       @PathVariable UUID trackId) {
        service.unlike(userId, trackId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/tracks/{trackId}")
    public ResponseEntity<Map<String, Boolean>> isLiked(@RequestHeader("X-User-Id") UUID userId,
                                                        @PathVariable UUID trackId) {
        return ResponseEntity.ok(Map.of("liked", service.isLiked(userId, trackId)));
    }

    @GetMapping("/tracks")
    public ResponseEntity<List<TrackResponseDto>> list(@RequestHeader("X-User-Id") UUID userId) {
        return ResponseEntity.ok(service.getLiked(userId));
    }
}

