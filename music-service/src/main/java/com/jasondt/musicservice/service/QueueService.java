package com.jasondt.musicservice.service;

import com.jasondt.musicservice.dto.QueueItemResponseDto;
import com.jasondt.musicservice.dto.TrackResponseDto;
import com.jasondt.musicservice.exception.NotFoundException;
import com.jasondt.musicservice.mapper.TrackMapper;
import com.jasondt.musicservice.model.Album;
import com.jasondt.musicservice.model.Playlist;
import com.jasondt.musicservice.model.PlaylistTrack;
import com.jasondt.musicservice.model.QueueItem;
import com.jasondt.musicservice.model.Track;
import com.jasondt.musicservice.repository.AlbumRepository;
import com.jasondt.musicservice.repository.PlaylistRepository;
import com.jasondt.musicservice.repository.QueueItemRepository;
import com.jasondt.musicservice.repository.TrackRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class QueueService {

    private final QueueItemRepository queueRepo;
    private final TrackRepository trackRepo;
    private final AlbumRepository albumRepo;
    private final PlaylistRepository playlistRepo;
    private final TrackMapper trackMapper;
    private final NowPlayingService nowPlayingService;
    private final HistoryService historyService;

    @Transactional
    public void addTrack(UUID userId, UUID trackId) {
        Track track = trackRepo.findByIdAndDeletedFalse(trackId)
                .orElseThrow(() -> new NotFoundException("Track not found with ID: " + trackId));
        long nextPos = queueRepo.findTopByUserIdOrderByPositionDesc(userId)
                .map(q -> q.getPosition() + 1)
                .orElse(1L);
        QueueItem qi = new QueueItem();
        qi.setUserId(userId);
        qi.setTrack(track);
        qi.setPosition(nextPos);
        queueRepo.save(qi);
    }

    @Transactional
    public void addAlbum(UUID userId, UUID albumId) {
        Album album = albumRepo.findByIdAndDeletedFalse(albumId)
                .orElseThrow(() -> new NotFoundException("Album not found with ID: " + albumId));
        long nextPos = queueRepo.findTopByUserIdOrderByPositionDesc(userId)
                .map(q -> q.getPosition() + 1)
                .orElse(1L);
        for (Track t : album.getTracks()) {
            QueueItem qi = new QueueItem();
            qi.setUserId(userId);
            qi.setTrack(t);
            qi.setPosition(nextPos++);
            queueRepo.save(qi);
        }
    }

    @Transactional
    public void addPlaylist(UUID userId, UUID playlistId) {
        Playlist p = playlistRepo.findByIdAndDeletedFalse(playlistId)
                .orElseThrow(() -> new NotFoundException("Playlist not found with ID: " + playlistId));
        long nextPos = queueRepo.findTopByUserIdOrderByPositionDesc(userId)
                .map(q -> q.getPosition() + 1)
                .orElse(1L);

        List<PlaylistTrack> pts = p.getTracks();
        if (pts != null) {
            pts.sort(Comparator.comparing(pt -> pt.getPosition() == null ? Integer.MAX_VALUE : pt.getPosition()));
            for (PlaylistTrack pt : pts) {
                QueueItem qi = new QueueItem();
                qi.setUserId(userId);
                qi.setTrack(pt.getTrack());
                qi.setPosition(nextPos++);
                queueRepo.save(qi);
            }
        }
    }


    public List<QueueItemResponseDto> getQueue(UUID userId) {
        return queueRepo.findByUserIdOrderByPositionAsc(userId).stream()
                .map(q -> {
                    QueueItemResponseDto dto = new QueueItemResponseDto();
                    dto.setId(q.getId());
                    dto.setTrack(trackMapper.toDto(q.getTrack()));
                    return dto;
                })
                .toList();
    }


    @Transactional
    public TrackResponseDto popNext(UUID userId) {
        QueueItem head = queueRepo.findTopByUserIdOrderByPositionAsc(userId)
                .orElseThrow(() -> new NotFoundException("Queue is empty"));
        UUID trackId = head.getTrack().getId();
        TrackResponseDto dto = nowPlayingService.setNowPlaying(userId, trackId);
        historyService.recordPlay(userId, trackId);
        queueRepo.delete(head);
        return dto;
    }

    @Transactional
    public void removeTrack(UUID userId, UUID queueItemId) {
        QueueItem qi = queueRepo.findById(queueItemId)
                .orElseThrow(() -> new NotFoundException("Queue item not found with ID: " + queueItemId));
        if (!qi.getUserId().equals(userId)) {
            throw new NotFoundException("Queue item does not belong to user");
        }
        queueRepo.delete(qi);
    }

    @Transactional
    public void clear(UUID userId) {
        queueRepo.deleteByUserId(userId);
    }

    @Transactional
    public List<QueueItemResponseDto> popBeforeIndex(UUID userId, int index) {
        List<QueueItem> items = queueRepo.findByUserIdOrderByPositionAsc(userId);
        if (items.isEmpty()) return List.of();
        if (index <= 0) {
            return getQueue(userId);
        }
        if (index >= items.size()) {
            queueRepo.deleteByUserId(userId);
            return List.of();
        }
        QueueItem target = items.get(index);
        Long cutoffPosition = target.getPosition();
        queueRepo.deleteByUserIdAndPositionLessThan(userId, cutoffPosition);
        return getQueue(userId);
    }
}
