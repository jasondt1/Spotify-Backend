package com.jasondt.musicservice.service;

import com.jasondt.musicservice.client.UserClient;
import com.jasondt.musicservice.dto.*;
import com.jasondt.musicservice.mapper.SimpleMapper;
import com.jasondt.musicservice.mapper.TrackMapper;
import com.jasondt.musicservice.model.*;
import com.jasondt.musicservice.repository.AlbumRepository;
import com.jasondt.musicservice.repository.ArtistRepository;
import com.jasondt.musicservice.repository.PlaylistRepository;
import com.jasondt.musicservice.repository.TrackRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class SearchService {

    private final ArtistRepository artistRepo;
    private final AlbumRepository albumRepo;
    private final PlaylistRepository playlistRepo;
    private final TrackRepository trackRepo;
    private final TrackMapper trackMapper;
    private final UserClient userClient;
    private final SimpleMapper simpleMapper;


    public SearchResponseDto search(String q) {
        String term = q == null ? "" : q.trim();
        SearchResponseDto resp = new SearchResponseDto();

        List<Artist> artists = artistRepo.findTop10ByDeletedFalseAndNameContainingIgnoreCase(term);
        List<Album> albums = albumRepo.findTop10ByDeletedFalseAndTitleContainingIgnoreCase(term);
        List<Playlist> playlists = playlistRepo.findTop10ByDeletedFalseAndNameContainingIgnoreCase(term);
        List<Track> tracks = trackRepo.findTop10ByDeletedFalseAndTitleContainingIgnoreCase(term);

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
