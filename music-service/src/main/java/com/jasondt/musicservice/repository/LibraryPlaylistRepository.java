package com.jasondt.musicservice.repository;

import com.jasondt.musicservice.model.Library;
import com.jasondt.musicservice.model.LibraryPlaylist;
import com.jasondt.musicservice.model.Playlist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface LibraryPlaylistRepository extends JpaRepository<LibraryPlaylist, UUID> {
    Optional<LibraryPlaylist> findByLibraryAndPlaylist(Library library, Playlist playlist);
}

