package com.jasondt.musicservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "library_albums", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"library_id", "album_id"})
})
@Getter
@Setter
@NoArgsConstructor
public class LibraryAlbum {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "library_id")
    private Library library;

    @ManyToOne(optional = false)
    @JoinColumn(name = "album_id")
    private Album album;

    @Column(name = "last_played_at")
    private Instant lastPlayedAt;

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
}

