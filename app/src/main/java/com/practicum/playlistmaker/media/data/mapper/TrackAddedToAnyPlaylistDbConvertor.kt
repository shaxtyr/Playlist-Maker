package com.practicum.playlistmaker.media.data.mapper

import com.practicum.playlistmaker.media.data.db.TrackAddedToAnyPlaylistEntity
import com.practicum.playlistmaker.search.domain.entity.Track

class TrackAddedToAnyPlaylistDbConvertor {

    fun map(track: Track): TrackAddedToAnyPlaylistEntity {
        return TrackAddedToAnyPlaylistEntity(
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

    fun map(track: TrackAddedToAnyPlaylistEntity): Track {
        return Track(
            track.previewUrl,
            track.collectionName,
            track.releaseDate,
            track.primaryGenreName,
            track.country,
            track.trackId,
            track.trackName,
            track.artistName,
            track.trackTime,
            track.artworkUrl100)
    }

}