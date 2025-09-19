package com.jasondt.musicservice.repository;

import com.jasondt.musicservice.model.Library;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface LibraryRepository extends JpaRepository<Library, UUID> {
    Optional<Library> findByUserId(UUID userId);
}
