package com.practicum.playlistmaker.media.domain.entity

data class Playlist(
    val playlistId: Long,
    val playlistName: String,
    val playlistDescription: String?,
    val imagePath: String?,
    val listIdTracks: List<Long>,
    val numberOfTracks: Long
)
