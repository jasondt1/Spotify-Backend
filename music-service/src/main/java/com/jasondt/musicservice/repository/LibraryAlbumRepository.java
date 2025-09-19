package com.jasondt.musicservice.repository;

import com.jasondt.musicservice.model.Album;
import com.jasondt.musicservice.model.Library;
import com.jasondt.musicservice.model.LibraryAlbum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface LibraryAlbumRepository extends JpaRepository<LibraryAlbum, UUID> {
    Optional<LibraryAlbum> findByLibraryAndAlbum(Library library, Album album);
}

