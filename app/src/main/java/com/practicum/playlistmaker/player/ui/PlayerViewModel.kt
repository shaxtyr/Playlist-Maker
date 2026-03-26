package com.practicum.playlistmaker.player.ui

import android.media.MediaPlayer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.media.domain.entity.Playlist
import com.practicum.playlistmaker.media.domain.interactor.FavoriteTracksInteractor
import com.practicum.playlistmaker.media.domain.interactor.PlaylistInteractor
import com.practicum.playlistmaker.media.ui.FavoriteTracksState
import com.practicum.playlistmaker.media.ui.PlaylistState
import com.practicum.playlistmaker.search.domain.entity.Track
import com.practicum.playlistmaker.search.ui.TracksState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(
    private val track: Track,
    private val mediaPlayer: MediaPlayer,
    private val trackFavoriteTracksInteractor: FavoriteTracksInteractor,
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    private var timerJob: Job? = null
    private val playerStateLiveData = MutableLiveData<PlayerState>(PlayerState(EnumStateMode.DEFAULT, getCurrentPlayerProgress(), false))
    fun observePlayerState(): LiveData<PlayerState> = playerStateLiveData

    private val playlistStateLiveData = MutableLiveData<PlaylistState>()
    fun observePlaylistState(): LiveData<PlaylistState> = playlistStateLiveData

    init {
        preparedPlayer()
    }

    private fun preparedPlayer() {
        mediaPlayer.reset()
        mediaPlayer.setDataSource(track.previewUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playerStateLiveData.postValue(PlayerState(EnumStateMode.PREPARED, getCurrentPlayerProgress(), track.isFavorite))
        }
        mediaPlayer.setOnCompletionListener {
            timerJob?.cancel()
            playerStateLiveData.postValue(PlayerState(EnumStateMode.PREPARED, "00:00", track.isFavorite))
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        playerStateLiveData.postValue(PlayerState(EnumStateMode.PLAYING, getCurrentPlayerProgress(), track.isFavorite))
        startTimer()
    }

    fun pausePlayer() {
        mediaPlayer.pause()
        timerJob?.cancel()
        playerStateLiveData.postValue(PlayerState(EnumStateMode.PAUSED, getCurrentPlayerProgress(), track.isFavorite))
    }

    override fun onCleared() {
        super.onCleared()
        mediaPlayer.release()
    }

    fun playbackControl() {
        when(playerStateLiveData.value?.stateMode) {
            EnumStateMode.PLAYING -> {
                pausePlayer()
            }
            else -> {
                startPlayer()
            }
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (mediaPlayer.isPlaying) {
                delay(DELAY)
                playerStateLiveData.postValue(PlayerState(EnumStateMode.PLAYING, getCurrentPlayerProgress(), track.isFavorite))
            }
        }
    }

    private fun getCurrentPlayerProgress(): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer.currentPosition) ?: "00:00"
    }

    fun onFavoriteClicked() {

        viewModelScope.launch {

            val currentFavorite = track.isFavorite
            if (currentFavorite) {
                trackFavoriteTracksInteractor.removeFromFavorite(track)
                track.isFavorite = false
            } else {
                trackFavoriteTracksInteractor.addToFavorite(track)
                track.isFavorite = true
            }

            playerStateLiveData.postValue(PlayerState(playerStateLiveData.value!!.stateMode,getCurrentPlayerProgress(), track.isFavorite))
        }
    }

    fun getPlaylists(){
        viewModelScope.launch {
            playlistInteractor
                .getPlaylists()
                .collect { playlists ->
                    processResult(playlists, "")
                }
        }
    }


    private fun processResult(currentPlaylists: List<Playlist>?, error: String) {
        val playlists = mutableListOf<Playlist>()

        if (currentPlaylists != null) {
            playlists.clear()
            playlists.addAll(currentPlaylists)
        }

        when {

            playlists.isEmpty() -> {
                renderPlaylistsState(
                    PlaylistState.Empty(
                        message = error
                    )
                )
            }
            else -> {
                renderPlaylistsState(
                    PlaylistState.Content(
                        playlists
                    )
                )
            }

        }
    }

    private fun renderPlaylistsState(state: PlaylistState) {
        playlistStateLiveData.postValue(state)
    }

    companion object {
        private const val DELAY = 300L
    }

}