package com.practicum.playlistmaker.media.data.mapper

import com.practicum.playlistmaker.media.data.db.TrackAddedToAnyPlaylistEntity
import com.practicum.playlistmaker.media.data.dto.TrackAddedToAnyPlaylistDto
import com.practicum.playlistmaker.search.domain.entity.Track

class TrackAddedToAnyPlaylistDbConvertor {

    fun toDomain(trackAddedToAnyPlaylistDto: TrackAddedToAnyPlaylistDto): Track {
        return Track(
            trackAddedToAnyPlaylistDto.previewUrl,
            trackAddedToAnyPlaylistDto.collectionName,
            trackAddedToAnyPlaylistDto.releaseDate,
            trackAddedToAnyPlaylistDto.primaryGenreName,
            trackAddedToAnyPlaylistDto.country,
            trackAddedToAnyPlaylistDto.trackId,
            trackAddedToAnyPlaylistDto.trackName,
            trackAddedToAnyPlaylistDto.artistName,
            trackAddedToAnyPlaylistDto.trackTime,
            trackAddedToAnyPlaylistDto.artworkUrl100)
    }

    fun toData(track: Track): TrackAddedToAnyPlaylistDto {
        return TrackAddedToAnyPlaylistDto(
            track.trackId,
            track.artworkUrl100,
            track.trackName,
            track.artistName,
            track.collectionName,
            track.releaseDate,
            track.primaryGenreName,
            track.country,
            track.trackTime,
            track.previewUrl)
    }

    fun map(trackAddedToAnyPlaylistDto: TrackAddedToAnyPlaylistDto): TrackAddedToAnyPlaylistEntity {
        return TrackAddedToAnyPlaylistEntity(
            trackAddedToAnyPlaylistDto.trackId,
            trackAddedToAnyPlaylistDto.artworkUrl100,
            trackAddedToAnyPlaylistDto.trackName,
            trackAddedToAnyPlaylistDto.artistName,
            trackAddedToAnyPlaylistDto.collectionName,
            trackAddedToAnyPlaylistDto.releaseDate,
            trackAddedToAnyPlaylistDto.primaryGenreName,
            trackAddedToAnyPlaylistDto.country,
            trackAddedToAnyPlaylistDto.trackTime,
            trackAddedToAnyPlaylistDto.previewUrl)
    }

    fun map(trackAddedToAnyPlaylistEntity: TrackAddedToAnyPlaylistEntity): TrackAddedToAnyPlaylistDto {
        return TrackAddedToAnyPlaylistDto(
            trackAddedToAnyPlaylistEntity.trackId,
            trackAddedToAnyPlaylistEntity.artworkUrl100,
            trackAddedToAnyPlaylistEntity.trackName,
            trackAddedToAnyPlaylistEntity.artistName,
            trackAddedToAnyPlaylistEntity.collectionName,
            trackAddedToAnyPlaylistEntity.releaseDate,
            trackAddedToAnyPlaylistEntity.primaryGenreName,
            trackAddedToAnyPlaylistEntity.country,
            trackAddedToAnyPlaylistEntity.trackTime,
            trackAddedToAnyPlaylistEntity.previewUrl)
    }

}