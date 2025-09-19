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
@Table(name = "libraries", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id"})
})
public class Library {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "user_id", nullable = false, unique = true)
    private UUID userId;

    @OneToMany(mappedBy = "library", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LibraryPlaylist> playlists = new ArrayList<>();

    @OneToMany(mappedBy = "library", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LibraryAlbum> albums = new ArrayList<>();

    @Column(name = "created_at", columnDefinition = "timestamp with time zone not null default now()", updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", columnDefinition = "timestamp with time zone not null default now()")
    private Instant updatedAt;

    public Library(UUID userId) {
        this.userId = userId;
    }

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

    public void addPlaylist(Playlist playlist) {
        if (this.playlists == null) this.playlists = new ArrayList<>();
        LibraryPlaylist link = new LibraryPlaylist();
        link.setLibrary(this);
        link.setPlaylist(playlist);
        this.playlists.add(link);
    }

    public void removePlaylist(Playlist playlist) {
        if (this.playlists == null) return;
        this.playlists.removeIf(lp -> lp.getPlaylist().getId().equals(playlist.getId()));
    }

    public void addAlbum(Album album) {
        if (this.albums == null) this.albums = new ArrayList<>();
        LibraryAlbum link = new LibraryAlbum();
        link.setLibrary(this);
        link.setAlbum(album);
        this.albums.add(link);
    }

    public void removeAlbum(Album album) {
        if (this.albums == null) return;
        this.albums.removeIf(la -> la.getAlbum().getId().equals(album.getId()));
    }
}
