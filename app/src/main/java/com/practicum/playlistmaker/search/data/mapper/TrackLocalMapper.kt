package com.practicum.playlistmaker.search.data.mapper

import com.practicum.playlistmaker.search.data.dto.TrackLocalDto
import com.practicum.playlistmaker.search.domain.entity.Track

object TrackLocalMapper {

    fun toDomain(trackLocalDto: TrackLocalDto): Track {
        return Track(
            previewUrl = trackLocalDto.previewUrl,
            collectionName = trackLocalDto.collectionName,
            releaseDate = trackLocalDto.releaseDate.substring(0, 4),
            primaryGenreName = trackLocalDto.primaryGenreName,
            country = trackLocalDto.country,
            trackId = trackLocalDto.trackId,
            trackName = trackLocalDto.trackName,
            artistName = trackLocalDto.artistName,
            trackTime = trackLocalDto.trackTime,
            artworkUrl100 = trackLocalDto.artworkUrl100
        )
    }

    fun toData(track: Track): TrackLocalDto {
        return TrackLocalDto(
            previewUrl = track.previewUrl,
            collectionName = track.collectionName,
            releaseDate = track.releaseDate.substring(0, 4),
            primaryGenreName = track.primaryGenreName,
            country = track.country,
            trackId = track.trackId,
            trackName = track.trackName,
            artistName = track.artistName,
            trackTime = track.trackTime,
            artworkUrl100 = track.artworkUrl100
        )
    }

}