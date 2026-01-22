package com.practicum.playlistmaker.player.ui

import android.media.MediaPlayer
import android.os.Handler
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.search.domain.entity.Track
import kotlinx.coroutines.Runnable
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(private val track: Track, private val mediaPlayer: MediaPlayer, private val handler: Handler) : ViewModel() {

    companion object {
        const val DELAY = 500L
    }

    private var updateTimerRunnable: Runnable? = null
    private val playerStateLiveData = MutableLiveData<PlayerState>(PlayerState(EnumStateMode.DEFAULT, SimpleDateFormat("mm:ss", Locale.getDefault()).format(0L)))
    fun observePlayerState(): LiveData<PlayerState> = playerStateLiveData

    init {
        preparedPlayer()
    }

    private fun preparedPlayer() {
        mediaPlayer.reset()
        mediaPlayer.setDataSource(track.previewUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playerStateLiveData.postValue(PlayerState(EnumStateMode.PREPARED, SimpleDateFormat("mm:ss", Locale.getDefault()).format(0L)))
        }
        mediaPlayer.setOnCompletionListener {
            playerStateLiveData.postValue(PlayerState(EnumStateMode.PREPARED, SimpleDateFormat("mm:ss", Locale.getDefault()).format(0L)))
            stopTimer()
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        playerStateLiveData.postValue(PlayerState(EnumStateMode.PLAYING, SimpleDateFormat("mm:ss", Locale.getDefault()).format(0L)))
        startTimer()
    }

    fun pausePlayer() {
        mediaPlayer.pause()
        playerStateLiveData.postValue(PlayerState(EnumStateMode.PAUSED, SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer.currentPosition)))
        stopTimer()
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
        if (updateTimerRunnable == null) {
            updateTimerRunnable = createUpdateTimerTask()
        }
        handler.post(
            updateTimerRunnable!!
        )
    }

    private fun stopTimer() {
        updateTimerRunnable?.let {
            handler.removeCallbacks(it)
        }
    }

    private fun createUpdateTimerTask() : kotlinx.coroutines.Runnable {
        return object : Runnable {
            override fun run() {
                playerStateLiveData.postValue(PlayerState(EnumStateMode.PLAYING, SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer.currentPosition)))
                handler.postDelayed(this, DELAY)
            }
        }
    }

}