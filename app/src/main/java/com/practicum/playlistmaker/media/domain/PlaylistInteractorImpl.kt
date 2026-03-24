package com.practicum.playlistmaker.media.domain

import com.practicum.playlistmaker.media.domain.entity.Playlist
import com.practicum.playlistmaker.media.domain.interactor.PlaylistInteractor
import com.practicum.playlistmaker.media.domain.repository.PlaylistRepository
import kotlinx.coroutines.flow.Flow

class PlaylistInteractorImpl(
    private val playlistRepository: PlaylistRepository) : PlaylistInteractor {

    override suspend fun createPlaylist(playlist: Playlist) {
        playlistRepository.createPlaylist(playlist)
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        playlistRepository.updatePlaylist(playlist)
    }

    override fun getPlaylists(): Flow<List<Playlist>> {
        return playlistRepository.getPlaylists()
    }
}