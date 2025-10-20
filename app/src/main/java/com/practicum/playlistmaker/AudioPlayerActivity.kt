package com.practicum.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.Locale
import androidx.constraintlayout.widget.Group

class AudioPlayerActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_player)

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

        val intent = getIntent()
        val openTrack = intent.getSerializableExtra(OPEN_TRACK_KEY) as? Track

        Glide.with(applicationContext)
            .load(openTrack?.getCoverArtwork())
            .placeholder(R.drawable.placeholder_312)
            .into(coverAudioPlayer)

        trackNameAudioPlayer.text = openTrack?.trackName
        artistNameAudioPlayer.text = openTrack?.artistName
        timeValueAudioPlayer.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(
            openTrack?.trackTime
        )

        if (openTrack?.collectionName == null) {
            albumGroup.visibility = View.GONE
        } else {
            albumGroup.visibility = View.VISIBLE
            albumValueAudioPlayer.text = openTrack.collectionName
        }

        if (openTrack?.getYearFromReleaseDate() == null) {
            yearGroup.visibility = View.GONE
        } else {
            yearGroup.visibility = View.VISIBLE
            yearValueAudioPlayer.text = openTrack.getYearFromReleaseDate()
        }

        genreValueAudioPlayer.text = openTrack?.primaryGenreName
        countryValueAudioPlayer.text = openTrack?.country

        back.setOnClickListener {
            finish()
        }
    }
    companion object {
        const val OPEN_TRACK_KEY = "open_track"
    }
}