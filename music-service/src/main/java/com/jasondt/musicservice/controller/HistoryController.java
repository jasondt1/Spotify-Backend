package com.jasondt.musicservice.controller;

import com.jasondt.musicservice.dto.HistoryResponseDto;
import com.jasondt.musicservice.dto.PlayCountResponseDto;
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
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(service.getUserHistory(userId, page, size));
    }
}
