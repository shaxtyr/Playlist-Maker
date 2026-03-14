package com.practicum.playlistmaker.player.ui

import android.media.MediaPlayer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.media.domain.interactor.FavoriteTracksInteractor
import com.practicum.playlistmaker.search.domain.entity.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(
    private val track: Track,
    private val mediaPlayer: MediaPlayer,
    private val trackFavoriteTracksInteractor: FavoriteTracksInteractor
) : ViewModel() {

    private var timerJob: Job? = null
    private val playerStateLiveData = MutableLiveData<PlayerState>(PlayerState(EnumStateMode.DEFAULT, getCurrentPlayerProgress(), false))
    fun observePlayerState(): LiveData<PlayerState> = playerStateLiveData

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
            if (track.isFavorite) {
                trackFavoriteTracksInteractor.removeFromFavorite(track)
            } else {
                trackFavoriteTracksInteractor.addToFavorite(track)
            }
        }

        playerStateLiveData.postValue(PlayerState(playerStateLiveData.value!!.stateMode,getCurrentPlayerProgress(), !track.isFavorite))
    }

    companion object {
        private const val DELAY = 300L
    }

}