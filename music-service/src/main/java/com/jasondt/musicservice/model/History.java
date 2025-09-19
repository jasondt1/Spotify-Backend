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
@Table(name = "history", indexes = {
        @Index(name = "idx_history_user_time", columnList = "user_id,played_at DESC"),
        @Index(name = "idx_history_track", columnList = "track_id")
})
public class History {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "track_id", nullable = false)
    private Track track;

    @Column(name = "played_at", columnDefinition = "timestamp with time zone not null default now()")
    private Instant playedAt;

    @PrePersist
    protected void onCreate() {
        if (playedAt == null) playedAt = Instant.now();
    }
}
