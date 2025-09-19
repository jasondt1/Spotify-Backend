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
@Table(name = "liked_tracks",
        uniqueConstraints = @UniqueConstraint(name = "uk_user_track", columnNames = {"user_id", "track_id"}),
        indexes = {
                @Index(name = "idx_liked_user", columnList = "user_id"),
                @Index(name = "idx_liked_track", columnList = "track_id")
        })
public class LikedTrack {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "track_id", nullable = false)
    private Track track;

    @Column(name = "created_at", columnDefinition = "timestamp with time zone not null default now()", updatable = false)
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = Instant.now();
    }
}

