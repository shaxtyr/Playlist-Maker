package com.practicum.playlistmaker.player.ui

import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.Group
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.search.domain.entity.Track

class PlayerActivity: AppCompatActivity() {

    private lateinit var viewModel: PlayerViewModel
    private lateinit var buttonPlay: ImageButton
    private lateinit var currentTimeOfTrack: TextView
    private lateinit var openTrack: Track
    private lateinit var coverAudioPlayer: ImageView
    private lateinit var trackNameAudioPlayer: TextView
    private lateinit var artistNameAudioPlayer: TextView
    private lateinit var timeValueAudioPlayer: TextView
    private lateinit var albumValueAudioPlayer: TextView
    private lateinit var yearValueAudioPlayer: TextView
    private lateinit var genreValueAudioPlayer: TextView
    private lateinit var countryValueAudioPlayer: TextView
    private lateinit var albumGroup: Group
    private lateinit var yearGroup: Group

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_player)


        val back = findViewById<ImageView>(R.id.back_from_audio_player)
        coverAudioPlayer = findViewById<ImageView>(R.id.cover_from_cardview)
        trackNameAudioPlayer = findViewById<TextView>(R.id.track_name_audio_player)
        artistNameAudioPlayer = findViewById<TextView>(R.id.artist_name_audio_player)
        timeValueAudioPlayer = findViewById<TextView>(R.id.time_value_audio_player)
        albumValueAudioPlayer = findViewById<TextView>(R.id.album_value_audio_player)
        yearValueAudioPlayer = findViewById<TextView>(R.id.year_value_audio_player)
        genreValueAudioPlayer = findViewById<TextView>(R.id.genre_value_audio_player)
        countryValueAudioPlayer = findViewById<TextView>(R.id.country_value_audio_player)
        albumGroup = findViewById<Group>(R.id.albumGroup)
        yearGroup = findViewById<Group>(R.id.yearGroup)

        buttonPlay = findViewById(R.id.play_button)
        currentTimeOfTrack = findViewById(R.id.progress_bar_audio_player)

        val intent = getIntent()
        openTrack = intent.getSerializableExtra(OPEN_TRACK_KEY) as Track

        viewModel = ViewModelProvider(this, PlayerViewModel.getFactory(openTrack)).get(
            PlayerViewModel::class.java)

        setOtherInfoFromTrack()

        viewModel.observeState().observe(this) {
            when(it) {
                PlayerViewModel.STATE_PLAYING -> buttonPlay.setImageResource(R.drawable.ic_pause_100)
                PlayerViewModel.STATE_PAUSED -> buttonPlay.setImageResource(R.drawable.ic_play_100)

            }
        }

        viewModel.observeTimer().observe(this) {
            currentTimeOfTrack.text = it
        }

        buttonPlay.setOnClickListener {
            viewModel.playbackControl()
        }

        back.setOnClickListener {
            finish()
        }
    }

    private fun setOtherInfoFromTrack() {
        Glide.with(applicationContext)
            .load(openTrack.getCoverArtwork())
            .placeholder(R.drawable.placeholder_312)
            .into(coverAudioPlayer)

        trackNameAudioPlayer.text = openTrack.trackName
        artistNameAudioPlayer.text = openTrack.artistName
        timeValueAudioPlayer.text = openTrack.trackTime

        if (openTrack.collectionName.isEmpty()) {
            albumGroup.isVisible = false
        } else {
            albumGroup.isVisible = true
            albumValueAudioPlayer.text = openTrack.collectionName
        }

        yearGroup.isVisible = true
        yearValueAudioPlayer.text = openTrack.getYearFromReleaseDate()

        genreValueAudioPlayer.text = openTrack.primaryGenreName
        countryValueAudioPlayer.text = openTrack.country
    }

    override fun onPause() {
        super.onPause()
        viewModel.pausePlayer()
    }

    companion object {
        private const val OPEN_TRACK_KEY = "open_track"
    }
}