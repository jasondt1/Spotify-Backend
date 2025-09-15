package com.jasondt.musicservice.controller;

import com.jasondt.musicservice.dto.ArtistCreateDto;
import com.jasondt.musicservice.dto.ArtistResponseDto;
import com.jasondt.musicservice.service.ArtistService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/artists")
@AllArgsConstructor
@SecurityRequirement(name = "BearerAuth")
public class ArtistController {
    private final ArtistService service;

    @PostMapping
    public ResponseEntity<ArtistResponseDto> create(@RequestBody @Valid ArtistCreateDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createArtist(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ArtistResponseDto> get(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getArtist(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ArtistResponseDto> update(@PathVariable UUID id,
                                                    @RequestBody @Valid com.jasondt.musicservice.dto.ArtistUpdateDto dto) {
        return ResponseEntity.ok(service.updateArtist(id, dto));
    }

    @GetMapping
    public ResponseEntity<List<ArtistResponseDto>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.deleteArtist(id);
        return ResponseEntity.noContent().build();
    }
}
