package com.practicum.playlistmaker.player.ui

import android.media.MediaPlayer
import android.util.Log
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

    private var currentPlaylists: List<Playlist> = mutableListOf()
    private var timerJob: Job? = null
    private val playerStateLiveData = MutableLiveData<PlayerState>(PlayerState(
        EnumStateMode.DEFAULT,
        getCurrentPlayerProgress(),
        false,
        currentPlaylists,
        null))
    fun observePlayerState(): LiveData<PlayerState> = playerStateLiveData

    init {
        preparedPlayer()
    }

    private fun preparedPlayer() {
        mediaPlayer.reset()
        mediaPlayer.setDataSource(track.previewUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playerStateLiveData.postValue(PlayerState(
                EnumStateMode.PREPARED,
                getCurrentPlayerProgress(),
                track.isFavorite,
                currentPlaylists,
                null))
        }
        mediaPlayer.setOnCompletionListener {
            timerJob?.cancel()
            playerStateLiveData.postValue(PlayerState(
                EnumStateMode.PREPARED,
                "00:00",
                track.isFavorite,
                currentPlaylists,
                null))
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        playerStateLiveData.postValue(PlayerState(
            EnumStateMode.PLAYING,
            getCurrentPlayerProgress(),
            track.isFavorite,
            currentPlaylists,
            null))
        startTimer()
    }

    fun pausePlayer() {
        mediaPlayer.pause()
        timerJob?.cancel()
        playerStateLiveData.postValue(PlayerState(
            EnumStateMode.PAUSED,
            getCurrentPlayerProgress(),
            track.isFavorite,
            currentPlaylists,
            null))
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
                playerStateLiveData.postValue(PlayerState(
                    EnumStateMode.PLAYING,
                    getCurrentPlayerProgress(),
                    track.isFavorite,
                    currentPlaylists,
                    null))
            }
        }
    }

    private fun getCurrentPlayerProgress(): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer.currentPosition) ?: "00:00"
    }

    fun checkTrackIdInPlaylist(playlist: Playlist) {

        val trackId = track.trackId
        val listIdPlaylist = playlist.listIdTracks

        listIdPlaylist.forEach {
            if (it == trackId) {
                playerStateLiveData.postValue(PlayerState(
                    playerStateLiveData.value!!.stateMode,
                    getCurrentPlayerProgress(),
                    track.isFavorite,
                    currentPlaylists,
                    AddedTrackToPlaylistState.AlreadyInPlaylist(playlist.playlistName)))

                return
            }

        }

        viewModelScope.launch {

            playlistInteractor.addToPlaylist(track, playlist)

            playerStateLiveData.postValue(PlayerState(
                playerStateLiveData.value!!.stateMode,
                getCurrentPlayerProgress(),
                track.isFavorite,
                currentPlaylists,
                AddedTrackToPlaylistState.AddedToPlayList(playlist.playlistName)))

        }
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

            playerStateLiveData.postValue(PlayerState(
                playerStateLiveData.value!!.stateMode,
                getCurrentPlayerProgress(),
                track.isFavorite,
                currentPlaylists,
                null))
        }
    }

    fun getPlaylists(){
        viewModelScope.launch {
            playlistInteractor
                .getPlaylists()
                .collect { playlists ->
                    currentPlaylists = playlists
                }
        }

        playerStateLiveData.postValue(PlayerState(
            playerStateLiveData.value!!.stateMode,
            getCurrentPlayerProgress(),
            track.isFavorite,
            currentPlaylists,
            null))

    }

    companion object {
        private const val DELAY = 300L
    }

}