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
import com.practicum.playlistmaker.utils.Event
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MyPlaylistsViewModel(
    private val context: Context,
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    private val playlistStateLiveData = MutableLiveData<PlaylistState>()
    fun observePlaylistState(): LiveData<PlaylistState> = playlistStateLiveData

    private val navigateToPlaylistDetailsLiveData = MutableLiveData<Event<Playlist>>()
    val observeNavigateToPlaylistDetails: LiveData<Event<Playlist>> = navigateToPlaylistDetailsLiveData


    private val navigateToCreatePlaylistLiveData = MutableLiveData<Event<Playlist>>()
    val observeNavigateToCreatePlaylist: LiveData<Event<Playlist>> = navigateToCreatePlaylistLiveData
    private var isClickAllowed = true

    init {
        fillData()
    }

    fun onPlaylistClick(playlist: Playlist) {
        if (clickDebounce()) {
            navigateToPlaylistDetailsLiveData.value = Event(playlist)
        }
    }

    fun onPlaylistCreate(playlist: Playlist) {
        if (clickDebounce()) {
            navigateToCreatePlaylistLiveData.value = Event(playlist)
        }
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            viewModelScope.launch {
                delay(CLICK_DEBOUNCE_DELAY)
                isClickAllowed = true
            }
        }
        return current
    }

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
            fillData()
        }

    }

    private fun renderPlaylistState(state: PlaylistState) {
        playlistStateLiveData.postValue(state)
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L

    }

}