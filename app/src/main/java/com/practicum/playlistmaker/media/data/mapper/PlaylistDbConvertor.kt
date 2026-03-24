package com.practicum.playlistmaker.media.data.mapper

import com.practicum.playlistmaker.media.data.db.PlaylistEntity
import com.practicum.playlistmaker.media.domain.entity.Playlist

class PlaylistDbConvertor {

    fun map(playlist: Playlist): PlaylistEntity {
        return PlaylistEntity(
            playlist.playlistId,
            playlist.playlistName,
            playlist.playlistDescription,
            playlist.imagePath,
            playlist.listIdTracks,
            playlist.numberOfTracks
            )
    }

    fun map(playlist: PlaylistEntity): Playlist {
        return Playlist(
            playlist.playlistId,
            playlist.playlistName,
            playlist.playlistDescription,
            playlist.imagePath,
            playlist.listIdTracks,
            playlist.numberOfTracks
            )
    }

}