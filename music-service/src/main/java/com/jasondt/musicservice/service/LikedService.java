package com.jasondt.musicservice.service;

import com.jasondt.musicservice.dto.TrackResponseDto;
import com.jasondt.musicservice.exception.DatabaseException;
import com.jasondt.musicservice.exception.NotFoundException;
import com.jasondt.musicservice.mapper.TrackMapper;
import com.jasondt.musicservice.model.LikedTrack;
import com.jasondt.musicservice.model.Track;
import com.jasondt.musicservice.repository.LikedTrackRepository;
import com.jasondt.musicservice.repository.TrackRepository;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class LikedService {

    private final LikedTrackRepository likedRepo;
    private final TrackRepository trackRepo;
    private final TrackMapper trackMapper;

    @Transactional
    public void like(UUID userId, UUID trackId) {
        Track track = trackRepo.findByIdAndDeletedFalse(trackId)
                .orElseThrow(() -> new NotFoundException("Track not found with ID: " + trackId));
        if (likedRepo.existsByUserIdAndTrack_Id(userId, trackId)) {
            return;
        }
        try {
            LikedTrack like = new LikedTrack();
            like.setUserId(userId);
            like.setTrack(track);
            likedRepo.save(like);
        } catch (DataIntegrityViolationException e) {
        } catch (Exception e) {
            throw new DatabaseException("Failed to like track", e);
        }
    }

    @Transactional
    public void unlike(UUID userId, UUID trackId) {
        likedRepo.findByUserIdAndTrack_Id(userId, trackId)
                .ifPresent(likedRepo::delete);
    }

    public boolean isLiked(UUID userId, UUID trackId) {
        return likedRepo.existsByUserIdAndTrack_Id(userId, trackId);
    }

    public List<TrackResponseDto> getLiked(UUID userId) {
        return likedRepo.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(lt -> trackMapper.toDto(lt.getTrack()))
                .toList();
    }
}

