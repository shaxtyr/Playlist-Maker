package com.practicum.playlistmaker.media.ui.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.media.domain.entity.Playlist
import com.practicum.playlistmaker.media.domain.interactor.PlaylistInteractor
import kotlinx.coroutines.launch

class EditingPlaylistFragmentViewModel(
    private val playlistInteractor: PlaylistInteractor
) : CreatingPlaylistFragmentViewModel(playlistInteractor) {

    private val playlistLiveData = MutableLiveData<Playlist>()
    fun observePlaylist(): LiveData<Playlist> = playlistLiveData

    fun getPlaylistById(playlistId: Long) {
        viewModelScope.launch {
            val playlist = playlistInteractor.getPlaylistById(playlistId)
            playlistLiveData.value = playlist
        }
    }

    fun updatePlaylist(coverPath: String, playlistName: String, playlistDescription: String) {
        val currentPlaylist = playlistLiveData.value!!
        viewModelScope.launch {
            val updatedPlaylist = currentPlaylist.copy(
                playlistName = playlistName,
                playlistDescription = playlistDescription,
                imagePath = coverPath
            )
            playlistInteractor.updatePlaylist(updatedPlaylist)

            playlistLiveData.value = updatedPlaylist
        }

    }
}