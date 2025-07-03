package com.jasondt.musicservice.repository;

import com.jasondt.musicservice.model.Artist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ArtistRepository extends JpaRepository<Artist, UUID> {

}