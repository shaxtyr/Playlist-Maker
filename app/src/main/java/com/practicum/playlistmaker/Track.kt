package com.practicum.playlistmaker

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Track(
    val collectionName: String,                                                 //альбом
    val releaseDate: String,                                                    //дата релиза
    val primaryGenreName: String,                                               //жанр
    val country: String,                                                        //страна
    val trackId: Long,                                                          // id
    val trackName: String,                                                       // Название композиции
    val artistName: String,                                                      // Имя исполнителя
    @SerializedName("trackTimeMillis") val trackTime: Long,             // Продолжительность трека
    val artworkUrl100: String                                                   // Ссылка на изображение обложки
) : Serializable {
    fun getCoverArtwork() = artworkUrl100.replaceAfterLast('/',"512x512bb.jpg")
    fun getYearFromReleaseDate() = releaseDate.substring(0, 4)
}