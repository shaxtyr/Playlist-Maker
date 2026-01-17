package com.practicum.playlistmaker.search.data.mapper

import android.util.Log
import com.practicum.playlistmaker.search.data.dto.TrackNetDto
import com.practicum.playlistmaker.search.domain.entity.Track

object TrackNetMapper {
    fun toDomain(trackNetDto: TrackNetDto): Track? {

        if (trackNetDto.previewUrl == null) {
            return null
        }

        return Track(
            previewUrl = trackNetDto.previewUrl,
            collectionName = trackNetDto.collectionName,
            releaseDate = trackNetDto.releaseDate.substring(0, 4),
            primaryGenreName = trackNetDto.primaryGenreName,
            country = trackNetDto.country,
            trackId = trackNetDto.trackId,
            trackName = trackNetDto.trackName,
            artistName = trackNetDto.artistName,
            trackTime = trackNetDto.convertTrackTimeToMmss(),
            artworkUrl100 = trackNetDto.artworkUrl100
        )
    }

}