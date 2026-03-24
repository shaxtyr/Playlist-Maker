package com.practicum.playlistmaker.media.domain.repository

import com.practicum.playlistmaker.media.domain.entity.Playlist
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {
    suspend fun createPlaylist(playlist: Playlist)
    suspend fun updatePlaylist(playlist: Playlist)

    fun getPlaylists(): Flow<List<Playlist>>
}