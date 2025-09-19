package com.jasondt.musicservice.controller;

import com.jasondt.musicservice.dto.SearchResponseDto;
import com.jasondt.musicservice.service.SearchService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/search")
@AllArgsConstructor
@SecurityRequirement(name = "BearerAuth")
public class SearchController {

    private final SearchService searchService;

    @GetMapping
    public ResponseEntity<SearchResponseDto> search(@RequestParam("q") String q) {
        return ResponseEntity.ok(searchService.search(q));
    }
}

