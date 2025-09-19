package com.jasondt.musicservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "now_playing", indexes = {
        @Index(name = "idx_now_playing_user", columnList = "user_id", unique = true)
})
public class NowPlaying {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "user_id", nullable = false, unique = true)
    private UUID userId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "track_id", nullable = false)
    private Track track;

    @Column(name = "started_at", columnDefinition = "timestamp with time zone not null default now()")
    private Instant startedAt;

    @Column(name = "position_sec", columnDefinition = "integer not null default 0")
    private Integer positionSec;

    @Column(name = "artist_id")
    private UUID artistId;

    @Column(name = "album_id")
    private UUID albumId;

    @Column(name = "playlist_id")
    private UUID playlistId;

    @PrePersist
    protected void onCreate() {
        if (startedAt == null) startedAt = Instant.now();
        if (positionSec == null) positionSec = 0;
    }
}
