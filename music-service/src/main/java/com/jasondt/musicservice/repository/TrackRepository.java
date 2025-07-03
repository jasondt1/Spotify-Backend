package com.jasondt.musicservice.repository;

import com.jasondt.musicservice.model.Track;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TrackRepository extends JpaRepository<Track, UUID> {
}
