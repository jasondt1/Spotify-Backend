package com.jasondt.musicservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Where;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "playlists")
public class Playlist {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "owner_id", nullable = false)
    private UUID ownerId;

    private String name;

    @Column(columnDefinition = "text")
    private String description;

    @Column(name = "image", columnDefinition = "text")
    private String image;

    @OneToMany(mappedBy = "playlist", cascade = CascadeType.ALL, orphanRemoval = true)
    @Where(clause = "deleted = false")
    private List<PlaylistTrack> tracks = new ArrayList<>();

    @Column(columnDefinition = "boolean not null default false")
    private boolean deleted = false;

    @Column(name = "created_at", columnDefinition = "timestamp with time zone not null default now()", updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", columnDefinition = "timestamp with time zone not null default now()")
    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }

    public void addPlaylistTrack(PlaylistTrack pt) {
        tracks.add(pt);
        pt.setPlaylist(this);
    }
}

