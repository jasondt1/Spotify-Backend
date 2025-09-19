package com.jasondt.musicservice.model;

import jakarta.persistence.*;
import java.time.Instant;
import org.hibernate.annotations.Where;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "artists")
public class Artist {

    @Id
    @GeneratedValue
    private UUID id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "genre_id")
    private Genre genre;

    @OneToMany(mappedBy = "artist", cascade = CascadeType.ALL, orphanRemoval = true)
    @Where(clause = "deleted = false")
    private List<Album> albums = new ArrayList<>();

    @Column(name = "image", columnDefinition = "text")
    private String image;

    @Column(name = "cover_image", columnDefinition = "text")
    private String coverImage;

    @OneToMany(mappedBy = "artist", cascade = CascadeType.ALL, orphanRemoval = true)
    @Where(clause = "deleted = false")
    private List<Track> tracks = new ArrayList<>();

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

    public void addAlbum(Album album) {
        albums.add(album);
        album.setArtist(this);
    }

    public void addTrack(Track track) {
        tracks.add(track);
        track.setArtist(this);
    }
}
