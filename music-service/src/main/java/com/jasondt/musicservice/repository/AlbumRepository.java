package com.jasondt.musicservice.repository;

import com.jasondt.musicservice.model.Album;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AlbumRepository extends JpaRepository<Album, UUID> {

}