package com.practicum.playlistmaker.media.data.dto

data class PlaylistDto(
    val playlistId: Long,
    val playlistName: String,
    val playlistDescription: String?,
    val imagePath: String?,
    val listIdTracks: List<Int>,
    val numberOfTracks: Int
)
