package com.practicum.playlistmaker.media.ui.viewModel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.media.domain.entity.Playlist
import com.practicum.playlistmaker.media.domain.interactor.PlaylistInteractor
import com.practicum.playlistmaker.media.ui.PlaylistState
import kotlinx.coroutines.launch

class MyPlaylistsViewModel(
    private val context: Context,
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    private val playlistStateLiveData = MutableLiveData<PlaylistState>()
    fun observePlaylistState(): LiveData<PlaylistState> = playlistStateLiveData

    fun fillData() {
        viewModelScope.launch {
            playlistInteractor
                .getPlaylists()
                .collect { playlists ->
                    processResult(playlists)
                }
        }
    }

    private fun processResult(playlists: List<Playlist>) {

        if (playlists.isEmpty()) {
            renderPlaylistState(
                PlaylistState.Empty(
                    message = context.getString(R.string.empty_my_playlists)
                )
            )
        } else {
            renderPlaylistState(
                PlaylistState.Content(playlists)
            )
        }

    }

    private fun renderPlaylistState(state: PlaylistState) {
        playlistStateLiveData.postValue(state)
    }

}