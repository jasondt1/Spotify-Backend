package com.jasondt.musicservice.repository;

import com.jasondt.musicservice.model.Genre;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GenreRepository extends JpaRepository<Genre, UUID> {
    List<Genre> findAllByDeletedFalse();
    Optional<Genre> findByIdAndDeletedFalse(UUID id);
}
