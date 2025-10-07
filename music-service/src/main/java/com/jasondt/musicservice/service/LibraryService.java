package com.jasondt.musicservice.service;

import com.jasondt.musicservice.client.UserClient;
import com.jasondt.musicservice.dto.ArtistSimpleDto;
import com.jasondt.musicservice.dto.LibraryResponseDto;
import com.jasondt.musicservice.dto.TrackResponseDto;
import com.jasondt.musicservice.dto.UserResponseDto;
import com.jasondt.musicservice.mapper.LibraryMapper;
import com.jasondt.musicservice.mapper.TrackMapper;
import com.jasondt.musicservice.mapper.SimpleMapper;
import com.jasondt.musicservice.model.Album;
import com.jasondt.musicservice.model.Library;
import com.jasondt.musicservice.model.LibraryAlbum;
import com.jasondt.musicservice.model.LibraryPlaylist;
import com.jasondt.musicservice.model.Playlist;
import com.jasondt.musicservice.model.PlaylistTrack;
import com.jasondt.musicservice.repository.AlbumRepository;
import com.jasondt.musicservice.repository.LibraryAlbumRepository;
import com.jasondt.musicservice.repository.LibraryPlaylistRepository;
import com.jasondt.musicservice.repository.LibraryRepository;
import com.jasondt.musicservice.repository.PlaylistRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
public class LibraryService {

    private final LibraryRepository libraryRepository;
    private final AlbumRepository albumRepository;
    private final PlaylistRepository playlistRepository;
    private final LibraryPlaylistRepository libraryPlaylistRepository;
    private final LibraryAlbumRepository libraryAlbumRepository;
    private final TrackMapper trackMapper;
    private final SimpleMapper simpleMapper;
    private final UserClient userClient;

    public LibraryService(LibraryRepository libraryRepository,
                          AlbumRepository albumRepository,
                          PlaylistRepository playlistRepository,
                          LibraryPlaylistRepository libraryPlaylistRepository,
                          LibraryAlbumRepository libraryAlbumRepository,
                          LibraryMapper libraryMapper,
                          TrackMapper trackMapper,
                          SimpleMapper simpleMapper,
                          UserClient userClient) {
        this.libraryRepository = libraryRepository;
        this.albumRepository = albumRepository;
        this.playlistRepository = playlistRepository;
        this.libraryPlaylistRepository = libraryPlaylistRepository;
        this.libraryAlbumRepository = libraryAlbumRepository;
        this.trackMapper = trackMapper;
        this.simpleMapper = simpleMapper;
        this.userClient = userClient;
    }

    @Transactional
    public Library ensureLibrary(UUID userId) {
        return libraryRepository.findByUserId(userId)
                .orElseGet(() -> libraryRepository.save(new Library(userId)));
    }

    @Transactional(readOnly = true)
    public List<LibraryResponseDto> getLibrary(UUID userId) {
        Library library = ensureLibrary(userId);
        class Pair {
            final LibraryResponseDto dto;
            final Instant ts;
            Pair(LibraryResponseDto d, Instant t) { dto = d; ts = t; }
        }

        List<Pair> pairs = new ArrayList<>();

        for (LibraryPlaylist lp : library.getPlaylists()) {
            Playlist p = lp.getPlaylist();
            if (p == null || p.isDeleted()) continue;
            LibraryResponseDto dto = new LibraryResponseDto();
            dto.setId(p.getId());
            dto.setType("playlist");
            dto.setName(p.getName());
            dto.setImage(p.getImage());
            try {
                UserResponseDto owner = userClient.getUserById(p.getOwnerId());
                dto.setCreator(owner == null ? null : owner.getName());
            } catch (Exception e) {
                dto.setCreator(null);
            }
            List<TrackResponseDto> tracks = new ArrayList<>();
            List<PlaylistTrack> pts = p.getTracks();
            if (pts != null) {
                pts.stream()
                        .sorted(Comparator.comparing(pt -> pt.getPosition() == null ? Integer.MAX_VALUE : pt.getPosition()))
                        .map(PlaylistTrack::getTrack)
                        .map(this::toTrackWithoutLyrics)
                        .forEach(tracks::add);
            }
            dto.setTracks(tracks);
            pairs.add(new Pair(dto, lp.getLastPlayedAt()));
        }
        for (LibraryAlbum la : library.getAlbums()) {
            Album a = la.getAlbum();
            if (a == null || a.isDeleted()) continue;
            LibraryResponseDto dto = new LibraryResponseDto();
            dto.setId(a.getId());
            dto.setType("album");
            dto.setName(a.getTitle());
            dto.setImage(a.getImage());
            dto.setCreator(a.getArtist() == null ? null : a.getArtist().getName());
            List<TrackResponseDto> tracks = new ArrayList<>();
            if (a.getTracks() != null) {
                a.getTracks().stream()
                        .map(this::toTrackWithoutLyrics)
                        .forEach(tracks::add);
            }
            dto.setTracks(tracks);
            pairs.add(new Pair(dto, la.getLastPlayedAt()));
        }
        pairs.sort((x, y) -> Comparator.nullsLast(Comparator.<Instant>naturalOrder())
                .reversed().compare(x.ts, y.ts));
        return pairs.stream().map(p -> p.dto).toList();
    }

    @Transactional
    public List<LibraryResponseDto> addPlaylist(UUID userId, UUID playlistId) {
        Library library = ensureLibrary(userId);
        Playlist playlist = playlistRepository.findByIdAndDeletedFalse(playlistId)
                .orElseThrow(() -> new IllegalArgumentException("Playlist not found"));
        var existing = libraryPlaylistRepository.findByLibraryAndPlaylist(library, playlist);
        LibraryPlaylist link;
        if (existing.isEmpty()) {
            link = new LibraryPlaylist();
            link.setLibrary(library);
            link.setPlaylist(playlist);
            library.getPlaylists().add(link);
        } else {
            link = existing.get();
        }
        link.setLastPlayedAt(Instant.now());
        libraryRepository.save(library);
        return getLibrary(userId);
    }


    @Transactional
    public List<LibraryResponseDto> removePlaylist(UUID userId, UUID playlistId) {
        Library library = ensureLibrary(userId);
        Playlist playlist = playlistRepository.findByIdAndDeletedFalse(playlistId)
                .orElseThrow(() -> new IllegalArgumentException("Playlist not found"));
        library.removePlaylist(playlist);
        libraryRepository.save(library);
        return getLibrary(userId);
    }

    @Transactional
    public List<LibraryResponseDto> addAlbum(UUID userId, UUID albumId) {
        Library library = ensureLibrary(userId);
        Album album = albumRepository.findByIdAndDeletedFalse(albumId)
                .orElseThrow(() -> new IllegalArgumentException("Album not found"));
        var existing = libraryAlbumRepository.findByLibraryAndAlbum(library, album);
        LibraryAlbum link;
        if (existing.isEmpty()) {
            link = new LibraryAlbum();
            link.setLibrary(library);
            link.setAlbum(album);
            library.getAlbums().add(link);
        } else {
            link = existing.get();
        }
        link.setLastPlayedAt(Instant.now());
        libraryRepository.save(library);
        return getLibrary(userId);
    }


    @Transactional
    public List<LibraryResponseDto> removeAlbum(UUID userId, UUID albumId) {
        Library library = ensureLibrary(userId);
        Album album = albumRepository.findByIdAndDeletedFalse(albumId)
                .orElseThrow(() -> new IllegalArgumentException("Album not found"));
        library.removeAlbum(album);
        libraryRepository.save(library);
        return getLibrary(userId);
    }

    @Transactional
    public void markPlaylistPlayed(UUID userId, UUID playlistId) {
        Library library = ensureLibrary(userId);
        Playlist playlist = playlistRepository.findByIdAndDeletedFalse(playlistId)
                .orElseThrow(() -> new IllegalArgumentException("Playlist not found"));
        LibraryPlaylist link = libraryPlaylistRepository.findByLibraryAndPlaylist(library, playlist)
                .orElseGet(() -> {
                    LibraryPlaylist lp = new LibraryPlaylist();
                    lp.setLibrary(library);
                    lp.setPlaylist(playlist);
                    library.getPlaylists().add(lp);
                    return lp;
                });
        link.setLastPlayedAt(Instant.now());
        libraryRepository.save(library);
    }

    @Transactional
    public void markAlbumPlayed(UUID userId, UUID albumId) {
        Library library = ensureLibrary(userId);
        Album album = albumRepository.findByIdAndDeletedFalse(albumId)
                .orElseThrow(() -> new IllegalArgumentException("Album not found"));
        LibraryAlbum link = libraryAlbumRepository.findByLibraryAndAlbum(library, album)
                .orElseGet(() -> {
                    LibraryAlbum la = new LibraryAlbum();
                    la.setLibrary(library);
                    la.setAlbum(album);
                    library.getAlbums().add(la);
                    return la;
                });
        link.setLastPlayedAt(Instant.now());
        libraryRepository.save(library);
    }

    private TrackResponseDto toTrackWithoutLyrics(com.jasondt.musicservice.model.Track t) {
        if (t == null) return null;
        TrackResponseDto dto = new TrackResponseDto();
        dto.setId(t.getId());
        dto.setTitle(t.getTitle());
        dto.setDuration(t.getDuration() == null ? 0 : t.getDuration());
        dto.setAudio(t.getAudio());
        dto.setCreatedAt(t.getCreatedAt());
        dto.setUpdatedAt(t.getUpdatedAt());

        List<ArtistSimpleDto> artists = new java.util.ArrayList<>();
        if (t.getArtist() != null && !t.getArtist().isDeleted()) {
            artists.add(simpleMapper.toArtistSimpleDto(t.getArtist()));
        }
        if (t.getOtherArtists() != null) {
            for (com.jasondt.musicservice.model.Artist a : t.getOtherArtists()) {
                if (a != null && !a.isDeleted()) {
                    if (t.getArtist() != null && a.getId() != null && a.getId().equals(t.getArtist().getId())) {
                        continue;
                    }
                    artists.add(simpleMapper.toArtistSimpleDto(a));
                }
            }
        }
        dto.setArtists(artists);

        if (t.getAlbum() != null) {
            com.jasondt.musicservice.dto.AlbumSimpleDto a = new com.jasondt.musicservice.dto.AlbumSimpleDto();
            a.setId(t.getAlbum().getId());
            a.setTitle(t.getAlbum().getTitle());
            a.setImage(t.getAlbum().getImage());
            a.setReleaseDate(t.getAlbum().getReleaseDate());
            dto.setAlbum(a);
        }

        dto.setLyrics(null);
        return dto;
    }
}
