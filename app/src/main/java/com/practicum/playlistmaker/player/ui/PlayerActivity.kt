package com.practicum.playlistmaker.player.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivityAudioPlayerBinding
import com.practicum.playlistmaker.search.domain.entity.Track

class PlayerActivity: AppCompatActivity() {

    private lateinit var binding: ActivityAudioPlayerBinding
    private lateinit var viewModel: PlayerViewModel
    private lateinit var openTrack: Track

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAudioPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = getIntent()
        openTrack = intent.getSerializableExtra(OPEN_TRACK_KEY) as Track

        viewModel = ViewModelProvider(this, PlayerViewModel.getFactory(openTrack)).get(
            PlayerViewModel::class.java)

        viewModel.observeState().observe(this) {
            when(it) {
                PlayerViewModel.STATE_PLAYING -> binding.playButton.setImageResource(R.drawable.ic_pause_100)
                PlayerViewModel.STATE_PAUSED -> binding.playButton.setImageResource(R.drawable.ic_play_100)

            }
        }

        setOtherInfoFromTrack()

        viewModel.observeTimer().observe(this) {
            binding.progressBarAudioPlayer.text = it
        }

        binding.playButton.setOnClickListener {
            viewModel.playbackControl()
        }

        binding.backFromAudioPlayer.setOnClickListener {
            finish()
        }
    }

    private fun setOtherInfoFromTrack() {
        Glide.with(applicationContext)
            .load(openTrack.getCoverArtwork())
            .placeholder(R.drawable.placeholder_312)
            .into(binding.coverFromCardview)

        binding.trackNameAudioPlayer.text = openTrack.trackName
        binding.artistNameAudioPlayer.text = openTrack.artistName
        binding.timeValueAudioPlayer.text = openTrack.trackTime

        if (openTrack.collectionName.isEmpty()) {
            binding.albumGroup.isVisible = false
        } else {
            binding.albumGroup.isVisible = true
            binding.albumValueAudioPlayer.text = openTrack.collectionName
        }

        binding.yearGroup.isVisible = true
        binding.yearValueAudioPlayer.text = openTrack.getYearFromReleaseDate()

        binding.genreValueAudioPlayer.text = openTrack.primaryGenreName
        binding.countryValueAudioPlayer.text = openTrack.country
    }

    override fun onPause() {
        super.onPause()
        viewModel.pausePlayer()
    }

    companion object {
        private const val OPEN_TRACK_KEY = "open_track"
    }
}