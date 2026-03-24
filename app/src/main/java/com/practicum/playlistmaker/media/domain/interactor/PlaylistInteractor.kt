package com.practicum.playlistmaker.media.domain.interactor

import com.practicum.playlistmaker.media.domain.entity.Playlist
import kotlinx.coroutines.flow.Flow

interface PlaylistInteractor {
    suspend fun createPlaylist(playlist: Playlist)
    suspend fun updatePlaylist(playlist: Playlist)
    fun getPlaylists(): Flow<List<Playlist>>
}