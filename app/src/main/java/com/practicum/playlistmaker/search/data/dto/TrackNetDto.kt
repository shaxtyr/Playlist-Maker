package com.practicum.playlistmaker.search.data.dto

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.Locale

data class TrackNetDto(
    val previewUrl: String,
    val collectionName: String,                                                 //альбом
    val releaseDate: String,                                                    //дата релиза
    val primaryGenreName: String,                                               //жанр
    val country: String,                                                        //страна
    val trackId: Long,                                                          // id
    val trackName: String,                                                       // Название композиции
    val artistName: String,                                                      // Имя исполнителя
    @SerializedName("trackTimeMillis") val trackTime: Long,             // Продолжительность трека
    val artworkUrl100: String
) : Serializable {
    fun convertTrackTimeToMmss() = SimpleDateFormat("mm:ss", Locale.getDefault()).format(trackTime)
}