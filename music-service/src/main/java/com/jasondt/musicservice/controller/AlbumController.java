package com.jasondt.musicservice.controller;

import com.jasondt.musicservice.dto.AlbumRequestDto;
import com.jasondt.musicservice.dto.AlbumResponseDto;
import com.jasondt.musicservice.service.AlbumService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/albums")
@AllArgsConstructor
public class AlbumController {
    private final AlbumService service;

    @PostMapping
    public ResponseEntity<AlbumResponseDto> create(@RequestBody @Valid AlbumRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createAlbum(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AlbumResponseDto> get(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getAlbum(id));
    }

    @GetMapping
    public ResponseEntity<List<AlbumResponseDto>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }
}