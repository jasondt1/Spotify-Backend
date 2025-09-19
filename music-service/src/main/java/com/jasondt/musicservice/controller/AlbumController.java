package com.jasondt.musicservice.controller;

import com.jasondt.musicservice.dto.AlbumCreateDto;
import com.jasondt.musicservice.dto.AlbumResponseDto;
import com.jasondt.musicservice.dto.AlbumUpdateDto;
import com.jasondt.musicservice.service.AlbumService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
@SecurityRequirement(name = "BearerAuth")
public class AlbumController {
    private final AlbumService service;

    @PostMapping
    public ResponseEntity<AlbumResponseDto> create(@RequestBody @Valid AlbumCreateDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createAlbum(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AlbumResponseDto> get(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getAlbum(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AlbumResponseDto> update(@PathVariable UUID id,
                                                   @RequestBody @Valid AlbumUpdateDto dto) {
        return ResponseEntity.ok(service.updateAlbum(id, dto));
    }

    @GetMapping
    public ResponseEntity<List<AlbumResponseDto>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.deleteAlbum(id);
        return ResponseEntity.noContent().build();
    }
}
