package com.jasondt.musicservice.controller;

import com.jasondt.musicservice.dto.GenreCreateDto;
import com.jasondt.musicservice.dto.GenreResponseDto;
import com.jasondt.musicservice.dto.GenreUpdateDto;
import com.jasondt.musicservice.service.GenreService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/genres")
@AllArgsConstructor
@SecurityRequirement(name = "BearerAuth")
public class GenreController {
    private final GenreService service;

    @PostMapping
    public ResponseEntity<GenreResponseDto> create(@RequestBody @Valid GenreCreateDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @GetMapping
    public ResponseEntity<List<GenreResponseDto>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GenreResponseDto> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GenreResponseDto> update(@PathVariable UUID id,
                                                   @RequestBody @Valid GenreUpdateDto dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
