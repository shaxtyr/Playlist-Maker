package com.practicum.playlistmaker.media.domain.entity

data class Playlist(
    val playlistId: Long,
    val playlistName: String,
    val playlistDescription: String,
    val imagePath: String,
    val listIdTracks: String,
    val numberOfTracks: Long
)

fun tracksWord(numberOfTracks: Long): String {
    val lastDigit = numberOfTracks.toString().last().digitToInt()
    return if (numberOfTracks in 10..20) {
        " треков"
    } else {
        when (lastDigit) {
            0, 5, 6, 7, 8, 9 -> " треков"
            1 -> " трек"
            else -> " трека"
        }
    }
}
