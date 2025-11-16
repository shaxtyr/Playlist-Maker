package com.practicum.playlistmaker

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.Locale
import androidx.constraintlayout.widget.Group
import kotlinx.coroutines.Runnable

class AudioPlayerActivity: AppCompatActivity() {

    private lateinit var play: ImageButton
    private lateinit var currentTimeOfTrack: TextView
    private lateinit var openTrack: Track
    private var playerState = STATE_DEFAULT
    private val mediaPlayer = MediaPlayer()
    private var handler: Handler? = null
    private var updateTimerRunnable: Runnable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_player)

        handler = Handler(Looper.getMainLooper())

        val back = findViewById<ImageView>(R.id.back_from_audio_player)
        val coverAudioPlayer = findViewById<ImageView>(R.id.cover_from_cardview)
        val trackNameAudioPlayer = findViewById<TextView>(R.id.track_name_audio_player)
        val artistNameAudioPlayer = findViewById<TextView>(R.id.artist_name_audio_player)
        val timeValueAudioPlayer = findViewById<TextView>(R.id.time_value_audio_player)
        val albumValueAudioPlayer = findViewById<TextView>(R.id.album_value_audio_player)
        val yearValueAudioPlayer = findViewById<TextView>(R.id.year_value_audio_player)
        val genreValueAudioPlayer = findViewById<TextView>(R.id.genre_value_audio_player)
        val countryValueAudioPlayer = findViewById<TextView>(R.id.country_value_audio_player)
        val albumGroup = findViewById<Group>(R.id.albumGroup)
        val yearGroup = findViewById<Group>(R.id.yearGroup)
        play = findViewById(R.id.play_button)
        currentTimeOfTrack = findViewById(R.id.progress_bar_audio_player)

        val intent = getIntent()
        openTrack = intent.getSerializableExtra(OPEN_TRACK_KEY) as Track

        Glide.with(applicationContext)
            .load(openTrack.getCoverArtwork())
            .placeholder(R.drawable.placeholder_312)
            .into(coverAudioPlayer)

        trackNameAudioPlayer.text = openTrack.trackName
        artistNameAudioPlayer.text = openTrack.artistName
        timeValueAudioPlayer.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(
            openTrack.trackTime
        )

        if (openTrack.collectionName.isEmpty()) {
            albumGroup.visibility = View.GONE
        } else {
            albumGroup.visibility = View.VISIBLE
            albumValueAudioPlayer.text = openTrack.collectionName
        }

        yearGroup.visibility = View.VISIBLE
        yearValueAudioPlayer.text = openTrack.getYearFromReleaseDate()

        genreValueAudioPlayer.text = openTrack.primaryGenreName
        countryValueAudioPlayer.text = openTrack.country

        preparedPlayer()

        play.setOnClickListener {
            playbackControl()
        }

        back.setOnClickListener {
            finish()
        }
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopTimer()
        mediaPlayer.release()
    }

    private fun preparedPlayer() {
        mediaPlayer.setDataSource(openTrack.previewUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            play.isEnabled = true
            playerState = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            play.setImageResource(R.drawable.ic_play_100)
            playerState = STATE_PREPARED
            stopTimer()
            currentTimeOfTrack.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(0L)
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        play.setImageResource(R.drawable.ic_pause_100)
        playerState = STATE_PLAYING
        startTimer()
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        play.setImageResource(R.drawable.ic_play_100)
        playerState = STATE_PAUSED
        stopTimer()
    }

    private fun playbackControl() {
        when(playerState) {
            STATE_PLAYING -> {
                pausePlayer()
            }
            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
    }

    private fun startTimer() {
        if (updateTimerRunnable == null) {
            updateTimerRunnable = createUpdateTimerTask()
        }
        handler?.post(
            updateTimerRunnable!!
        )
    }

    private fun stopTimer() {
        updateTimerRunnable?.let {
            handler?.removeCallbacks(it)
        }
    }

    private fun createUpdateTimerTask() : Runnable {
        return object : Runnable {
            override fun run() {
                currentTimeOfTrack.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer.currentPosition)
                handler?.postDelayed(this, DELAY)
            }
        }
    }

    companion object {
        private const val OPEN_TRACK_KEY = "open_track"
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
        private const val DELAY = 500L
    }
}