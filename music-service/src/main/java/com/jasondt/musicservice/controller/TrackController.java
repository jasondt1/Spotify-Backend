package com.jasondt.musicservice.controller;

import com.jasondt.musicservice.dto.TrackCreateDto;
import com.jasondt.musicservice.dto.TrackResponseDto;
import com.jasondt.musicservice.dto.TrackUpdateDto;
import com.jasondt.musicservice.dto.TrackWithPlayCountResponseDto;
import com.jasondt.musicservice.service.TrackService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tracks")
@AllArgsConstructor
@SecurityRequirement(name = "BearerAuth")
public class TrackController {
    private final TrackService service;

    @PostMapping
    public ResponseEntity<TrackResponseDto> create(@RequestBody @Valid TrackCreateDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.addTrack(dto));
    }

    @GetMapping
    public ResponseEntity<List<TrackResponseDto>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TrackResponseDto> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TrackResponseDto> update(@PathVariable UUID id,
                                                   @RequestBody @Valid TrackUpdateDto dto) {
        return ResponseEntity.ok(service.updateTrack(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.deleteTrack(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/{id}/with-plays")
    public ResponseEntity<TrackWithPlayCountResponseDto> getWithPlays(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getTrackWithPlayCount(id));
    }

}
