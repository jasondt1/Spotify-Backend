package com.jasondt.musicservice.model;

import jakarta.persistence.*;
import org.hibernate.annotations.Where;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "albums")
public class Album {

    @Id
    @GeneratedValue
    private UUID id;

    private String title;

    private LocalDate releaseDate;

    @ManyToOne
    @JoinColumn(name = "artist_id", nullable = false)
    private Artist artist;

    @OneToMany(mappedBy = "album", cascade = CascadeType.ALL, orphanRemoval = true)
    @Where(clause = "deleted = false")
    private List<Track> tracks = new ArrayList<>();

    @Column(name = "image", columnDefinition = "text")
    private String image;

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

    public void addTrack(Track track) {
        tracks.add(track);
        track.setAlbum(this);
    }
}
