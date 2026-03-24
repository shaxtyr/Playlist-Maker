package com.practicum.playlistmaker.media.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlist_table")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true)
    val playlistId: Long = 0,
    val playlistName: String,
    val playlistDescription: String,
    val imagePath: String,
    val listIdTracks: String = "",
    val numberOfTracks: Long
)
