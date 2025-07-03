package com.jasondt.musicservice.controller;

import com.jasondt.musicservice.dto.TrackRequestDto;
import com.jasondt.musicservice.dto.TrackResponseDto;
import com.jasondt.musicservice.service.TrackService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tracks")
@AllArgsConstructor
public class TrackController {
    private final TrackService service;

    @PostMapping
    public ResponseEntity<TrackResponseDto> create(@RequestBody @Valid TrackRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.addTrack(dto));
    }

    @GetMapping
    public ResponseEntity<List<TrackResponseDto>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }
}
