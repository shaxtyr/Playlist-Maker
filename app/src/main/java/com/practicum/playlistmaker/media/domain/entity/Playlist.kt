package com.practicum.playlistmaker.media.domain.entity

data class Playlist(
    val playlistId: Long = 0,
    val playlistName: String = "",
    val playlistDescription: String = "",
    val imagePath: String = "",
    val listIdTracks: List<Int> = emptyList<Int>(),
    val numberOfTracks: Int = 0
)
