package com.practicum.playlistmaker.media.domain.interactor

import com.practicum.playlistmaker.media.domain.entity.Playlist
import com.practicum.playlistmaker.search.domain.entity.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistInteractor {
    suspend fun createPlaylist(playlist: Playlist)
    suspend fun updatePlaylist(playlist: Playlist)
    fun getPlaylists(): Flow<List<Playlist>>
    suspend fun addToPlaylist(track: Track, playlist: Playlist)
}