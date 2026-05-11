package com.practicum.playlistmaker.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.media.domain.entity.Playlist
import com.practicum.playlistmaker.player.ui.EnumStateMode
import com.practicum.playlistmaker.player.ui.PlayerControl
import com.practicum.playlistmaker.player.ui.PlayerState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MusicService : Service(), PlayerControl {

    private val binder = MusicServiceBinder()
    private var songUrl = ""
    private var trackName = ""
    private var artistName = ""
    private var timerJob: Job? = null
    private var mediaPlayer: MediaPlayer? = null
    private var currentPlaylists: List<Playlist> = mutableListOf()

    private val _playerState = MutableStateFlow<PlayerState>(PlayerState(
        EnumStateMode.DEFAULT,
        "00:00",
        false,
        currentPlaylists,
        null
    ))

    val playerState = _playerState.asStateFlow()



    override fun onCreate() {
        super.onCreate()

        createNotificationChannel()
        mediaPlayer = MediaPlayer()
    }

    override fun onBind(intent: Intent?): IBinder? {
        songUrl = intent?.getStringExtra(SONG_URL_KEY) ?: ""
        trackName = intent?.getStringExtra(TRACK_NAME_KEY) ?: ""
        artistName = intent?.getStringExtra(ARTIST_NAME_KEY) ?: ""

        initPlayer()

        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        releasePlayer()
        return super.onUnbind(intent)
    }

    override fun getPlayerStateFlow(): StateFlow<PlayerState> {
        return playerState
    }

    override fun startPlayer() {
        //initPlayer()
        mediaPlayer?.start()

        _playerState.update { it.copy(
            stateMode = EnumStateMode.PLAYING,
            progressTime = getCurrentPlayerProgress()) }

        /*_playerState.value = PlayerState(
            EnumStateMode.PLAYING,
            getCurrentPlayerProgress(),
            isFavorite,
            currentPlaylists,
            null)*/
        startTimer()
    }

    override fun pausePlayer() {
        mediaPlayer?.pause()
        timerJob?.cancel()

        hideNotification()
        _playerState.update { it.copy(
            stateMode = EnumStateMode.PAUSED,
            progressTime = getCurrentPlayerProgress()) }

        /*_playerState.value = PlayerState(
            EnumStateMode.PAUSED,
            getCurrentPlayerProgress(),
            isFavorite,
            currentPlaylists,
            null)*/
    }

    /*fun getCurrentPlayerProgress(): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer?.currentPosition) ?: "00:00"
    }*/

    fun getCurrentPlayerProgress(): String {
        val positionMs = mediaPlayer?.currentPosition ?: 0
        val date = Date(positionMs.toLong())
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(date)
    }


    private fun initPlayer() {
        if (songUrl.isEmpty()) return

        mediaPlayer?.reset()
        mediaPlayer?.setDataSource(songUrl)
        mediaPlayer?.prepareAsync()
        mediaPlayer?.setOnPreparedListener {

            _playerState.update { it.copy(
                stateMode = EnumStateMode.PREPARED,
                progressTime = getCurrentPlayerProgress()) }
        }
        mediaPlayer?.setOnCompletionListener {
            timerJob?.cancel()

            hideNotification()
            _playerState.update { it.copy(
                stateMode = EnumStateMode.PREPARED,
                progressTime = "00:00") }
        }
    }

    private fun releasePlayer() {
        timerJob?.cancel()
        mediaPlayer?.stop()

        _playerState.update { it.copy(
            stateMode = EnumStateMode.DEFAULT,
            progressTime = getCurrentPlayerProgress()) }

        /*_playerState.value = PlayerState(
            EnumStateMode.DEFAULT,
            getCurrentPlayerProgress(),
            false,
            currentPlaylists,
            null)*/

        mediaPlayer?.setOnPreparedListener(null)
        mediaPlayer?.setOnCompletionListener(null)
        mediaPlayer?.release()
        mediaPlayer = null
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = CoroutineScope(Dispatchers.Default).launch {
            while (mediaPlayer?.isPlaying == true) {
                delay(DELAY)

                _playerState.update { it.copy(
                    stateMode = EnumStateMode.PLAYING,
                    progressTime = getCurrentPlayerProgress()) }

                /*_playerState.value = PlayerState(
                    EnumStateMode.PLAYING,
                    getCurrentPlayerProgress(),
                    isFavorite,
                    currentPlaylists,
                    null)*/
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return
        }

        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NAME_CHANNEL,
            NotificationManager.IMPORTANCE_DEFAULT
        )
        channel.description = DESCRIPTION_CHANNEL

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun createServiceNotification(): Notification {
        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle(NOTIFICATION_TITLE)
            .setContentText("$trackName - $artistName")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .build()
    }

    private fun getForegroundServiceTypeConstant(): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
        } else {
            0
        }
    }

    override fun showNotification() {
        ServiceCompat.startForeground(
            this,
            SERVICE_NOTIFICATION_ID,
            createServiceNotification(),
            getForegroundServiceTypeConstant()
        )
    }

    override fun hideNotification() {
        ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
    }

    inner class MusicServiceBinder : Binder() {
        fun getService(): MusicService = this@MusicService
    }

    companion object {
        private const val DELAY = 300L
        private const val SONG_URL_KEY = "song_url"
        private const val TRACK_NAME_KEY = "track_name"
        private const val ARTIST_NAME_KEY = "artist_name"
        const val NOTIFICATION_CHANNEL_ID = "music_service_channel"
        const val NAME_CHANNEL = "music channel"
        const val DESCRIPTION_CHANNEL = "channel for playing music"
        const val NOTIFICATION_TITLE = "Playlist Maker"
        const val SERVICE_NOTIFICATION_ID = 101
    }
}