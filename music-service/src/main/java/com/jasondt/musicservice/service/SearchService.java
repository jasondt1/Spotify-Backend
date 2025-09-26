package com.jasondt.musicservice.service;

import com.jasondt.musicservice.client.UserClient;
import com.jasondt.musicservice.dto.*;
import com.jasondt.musicservice.mapper.SimpleMapper;
import com.jasondt.musicservice.mapper.TrackMapper;
import com.jasondt.musicservice.model.*;
import com.jasondt.musicservice.repository.*;
import com.jasondt.musicservice.search.DamerauLevenshtein;
import com.jasondt.musicservice.search.TextNormalizer;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class SearchService {

    private final ArtistRepository artistRepo;
    private final AlbumRepository albumRepo;
    private final PlaylistRepository playlistRepo;
    private final TrackRepository trackRepo;
    private final HistoryRepository historyRepo;
    private final TrackMapper trackMapper;
    private final UserClient userClient;
    private final SimpleMapper simpleMapper;

    private <T> List<T> rerank(List<T> items, Function<T,String> getName, String q, int limit) {
        String nq = TextNormalizer.normalize(q);
        return items.stream()
                .map(x -> {
                    String name = Optional.ofNullable(getName.apply(x)).orElse("");
                    String nn = TextNormalizer.normalize(name);
                    double sim = DamerauLevenshtein.similarity(nq, nn);
                    boolean contains = nn.contains(nq);
                    return Map.entry(x, Math.max(sim, contains ? 1.0 : sim));
                })
                .filter(e -> e.getValue() >= 0.60)
                .sorted((a,b) -> Double.compare(b.getValue(), a.getValue()))
                .limit(limit)
                .map(Map.Entry::getKey)
                .toList();
    }

    private List<Artist> searchArtists(String term) {
        List<Artist> base = artistRepo.findTop10ByDeletedFalseAndNameContainingIgnoreCase(term);
        if (base.size() >= 10) return base;
        String nq = TextNormalizer.normalize(term);
        if (nq.isEmpty()) return base;
        Set<Artist> acc = new LinkedHashSet<>(base);
        for (String tok : nq.split(" ")) {
            if (tok.isBlank()) continue;
            acc.addAll(artistRepo.findTop10ByDeletedFalseAndNameContainingIgnoreCase(tok));
            if (acc.size() >= 30) break;
        }
        return new ArrayList<>(acc);
    }

    private List<Album> searchAlbums(String term) {
        List<Album> base = albumRepo.findTop10ByDeletedFalseAndTitleContainingIgnoreCase(term);
        if (base.size() >= 10) return base;
        String nq = TextNormalizer.normalize(term);
        if (nq.isEmpty()) return base;
        Set<Album> acc = new LinkedHashSet<>(base);
        for (String tok : nq.split(" ")) {
            if (tok.isBlank()) continue;
            acc.addAll(albumRepo.findTop10ByDeletedFalseAndTitleContainingIgnoreCase(tok));
            if (acc.size() >= 30) break;
        }
        return new ArrayList<>(acc);
    }

    private List<Playlist> searchPlaylists(String term) {
        List<Playlist> base = playlistRepo.findTop10ByDeletedFalseAndNameContainingIgnoreCase(term);
        if (base.size() >= 10) return base;
        String nq = TextNormalizer.normalize(term);
        if (nq.isEmpty()) return base;
        Set<Playlist> acc = new LinkedHashSet<>(base);
        for (String tok : nq.split(" ")) {
            if (tok.isBlank()) continue;
            acc.addAll(playlistRepo.findTop10ByDeletedFalseAndNameContainingIgnoreCase(tok));
            if (acc.size() >= 30) break;
        }
        return new ArrayList<>(acc);
    }

    private List<Track> searchTracks(String term) {
        List<Track> base = trackRepo.findTop10ByDeletedFalseAndTitleContainingIgnoreCase(term);
        if (base.size() >= 10) return base;
        String nq = TextNormalizer.normalize(term);
        if (nq.isEmpty()) return base;
        Set<Track> acc = new LinkedHashSet<>(base);
        for (String tok : nq.split(" ")) {
            if (tok.isBlank()) continue;
            acc.addAll(trackRepo.findTop10ByDeletedFalseAndTitleContainingIgnoreCase(tok));
            if (acc.size() >= 30) break;
        }
        return new ArrayList<>(acc);
    }

    public SearchResponseDto search(String q) {
        String term = q == null ? "" : q.trim();
        if (term.isBlank()) return topOnlyResponse();
        SearchResponseDto resp = new SearchResponseDto();

        List<Artist> artists = rerank(searchArtists(term), Artist::getName, term, 50);
        List<Album> albums = rerank(searchAlbums(term), Album::getTitle, term, 50);
        List<Playlist> playlists = rerank(searchPlaylists(term), Playlist::getName, term, 50);
        List<Track> tracks = rerank(searchTracks(term), Track::getTitle, term, 50);

        resp.setArtists(artists.stream().map(simpleMapper::toArtistSimpleDto).collect(Collectors.toList()));
        resp.setAlbums(albums.stream().map(simpleMapper::toAlbumSimpleDto).collect(Collectors.toList()));
        resp.setPlaylists(playlists.stream().map(this::mapPlaylistWithOwner).collect(Collectors.toList()));
        resp.setTracks(new ArrayList<>(trackMapper.toDto(tracks)));

        resp.setTop(determineTopResult(artists, albums, playlists, tracks));

        if (!artists.isEmpty()) {
            List<UUID> artistIds = artists.stream().map(Artist::getId).collect(Collectors.toList());
            if (!artistIds.isEmpty()) {
                List<Album> artistAlbums = albumRepo.findTop10ByDeletedFalseAndArtistIdIn(artistIds);
                List<Track> artistTracks = trackRepo.findTop10ByDeletedFalseAndArtistIdIn(artistIds);
                resp.getAlbums().addAll(artistAlbums.stream().map(simpleMapper::toAlbumSimpleDto).toList());
                resp.getTracks().addAll(trackMapper.toDto(artistTracks));
            }
        }

        if (!albums.isEmpty()) {
            List<UUID> albumIds = albums.stream().map(Album::getId).collect(Collectors.toList());
            if (!albumIds.isEmpty()) {
                List<Track> albumTracks = trackRepo.findTop10ByDeletedFalseAndAlbumIdIn(albumIds);
                resp.getTracks().addAll(trackMapper.toDto(albumTracks));
            }
            List<ArtistSimpleDto> albumArtists = albums.stream()
                    .map(Album::getArtist)
                    .filter(Objects::nonNull)
                    .map(simpleMapper::toArtistSimpleDto)
                    .toList();
            resp.getArtists().addAll(albumArtists);
        }

        if (!tracks.isEmpty()) {
            List<ArtistSimpleDto> trackArtists = tracks.stream()
                    .map(Track::getArtist)
                    .filter(Objects::nonNull)
                    .map(simpleMapper::toArtistSimpleDto)
                    .toList();
            resp.getArtists().addAll(trackArtists);
            List<AlbumSimpleDto> trackAlbums = tracks.stream()
                    .map(Track::getAlbum)
                    .filter(Objects::nonNull)
                    .map(simpleMapper::toAlbumSimpleDto)
                    .toList();
            resp.getAlbums().addAll(trackAlbums);
        }

        if (!playlists.isEmpty()) {
            List<Track> playlistTracks = playlists.stream()
                    .flatMap(p -> Optional.ofNullable(p.getTracks()).orElseGet(List::of).stream())
                    .map(PlaylistTrack::getTrack)
                    .filter(Objects::nonNull)
                    .limit(100)
                    .toList();
            if (!playlistTracks.isEmpty()) {
                resp.getTracks().addAll(trackMapper.toDto(playlistTracks));
                List<ArtistSimpleDto> playlistArtists = playlistTracks.stream()
                        .map(Track::getArtist)
                        .filter(Objects::nonNull)
                        .map(simpleMapper::toArtistSimpleDto)
                        .toList();
                resp.getArtists().addAll(playlistArtists);
                List<AlbumSimpleDto> playlistAlbums = playlistTracks.stream()
                        .map(Track::getAlbum)
                        .filter(Objects::nonNull)
                        .map(simpleMapper::toAlbumSimpleDto)
                        .toList();
                resp.getAlbums().addAll(playlistAlbums);
            }
        }

        resp.setArtists(removeDuplicateArtists(resp.getArtists()));
        resp.setAlbums(removeDuplicateAlbums(resp.getAlbums()));
        resp.setTracks(removeDuplicateTracks(resp.getTracks()));

        return resp;
    }

    private SearchResponseDto topOnlyResponse() {
        SearchResponseDto resp = new SearchResponseDto();

        List<Track> topTracks = historyRepo.findTopTracks(PageRequest.of(0, 50));
        List<Artist> topArtists = historyRepo.findTopArtists(PageRequest.of(0, 50));
        List<Album> topAlbums = historyRepo.findTopAlbums(PageRequest.of(0, 50));

        List<UUID> topTrackIds = topTracks.stream().map(Track::getId).toList();
        List<Playlist> topPlaylists = topTrackIds.isEmpty()
                ? List.of()
                : playlistRepo.findTopByTrackIds(topTrackIds, PageRequest.of(0, 50));

        resp.setTracks(new ArrayList<>(trackMapper.toDto(topTracks)));
        resp.setArtists(topArtists.stream().map(simpleMapper::toArtistSimpleDto).toList());
        resp.setAlbums(topAlbums.stream().map(simpleMapper::toAlbumSimpleDto).toList());
        resp.setPlaylists(topPlaylists.stream().map(this::mapPlaylistWithOwner).toList());

        resp.setTop(determineTopResult(
                topArtists,
                topAlbums,
                topPlaylists,
                topTracks
        ));

        resp.setArtists(removeDuplicateArtists(resp.getArtists()));
        resp.setAlbums(removeDuplicateAlbums(resp.getAlbums()));
        resp.setTracks(removeDuplicateTracks(resp.getTracks()));

        return resp;
    }

    private TopSearchResultDto determineTopResult(
            List<Artist> artists,
            List<Album> albums,
            List<Playlist> playlists,
            List<Track> tracks
    ) {
        TopSearchResultDto top = new TopSearchResultDto();
        if (!artists.isEmpty()) {
            top.setType("artist");
            top.setArtist(simpleMapper.toArtistSimpleDto(artists.get(0)));
        } else if (!albums.isEmpty()) {
            top.setType("album");
            top.setAlbum(simpleMapper.toAlbumSimpleDto(albums.get(0)));
        } else if (!tracks.isEmpty()) {
            top.setType("track");
            top.setTrack(trackMapper.toDto(tracks.get(0)));
        } else if (!playlists.isEmpty()) {
            top.setType("playlist");
            top.setPlaylist(mapPlaylistWithOwner(playlists.get(0)));
        } else {
            return null;
        }
        return top;
    }

    private PlaylistSimpleDto mapPlaylistWithOwner(Playlist p) {
        PlaylistSimpleDto dto = simpleMapper.toPlaylistSimpleDto(p);
        try {
            UserResponseDto user = userClient.getUserById(p.getOwnerId());
            dto.setOwner(user);
        } catch (Exception e) {
            dto.setOwner(null);
        }
        if (p.getTracks() != null) {
            java.util.List<TrackResponseDto> tracks = p.getTracks().stream()
                    .sorted(java.util.Comparator.comparing(pt -> pt.getPosition() == null ? Integer.MAX_VALUE : pt.getPosition()))
                    .map(PlaylistTrack::getTrack)
                    .map(trackMapper::toDto)
                    .toList();
            dto.setTracks(tracks);
        }
        return dto;
    }

    private List<ArtistSimpleDto> removeDuplicateArtists(List<ArtistSimpleDto> list) {
        return new ArrayList<>(list.stream()
                .collect(Collectors.toMap(ArtistSimpleDto::getId, a -> a, (a, b) -> a))
                .values());
    }

    private List<AlbumSimpleDto> removeDuplicateAlbums(List<AlbumSimpleDto> list) {
        return new ArrayList<>(list.stream()
                .collect(Collectors.toMap(AlbumSimpleDto::getId, a -> a, (a, b) -> a))
                .values());
    }

    private List<TrackResponseDto> removeDuplicateTracks(List<TrackResponseDto> list) {
        return new ArrayList<>(list.stream()
                .collect(Collectors.toMap(TrackResponseDto::getId, t -> t, (a, b) -> a))
                .values());
    }
}
