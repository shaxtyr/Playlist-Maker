package com.practicum.playlistmaker.domain.entity

import java.io.Serializable

data class Track(
    val previewUrl: String,
    val collectionName: String,                                                 //альбом
    val releaseDate: String,                                                    //дата релиза
    val primaryGenreName: String,                                               //жанр
    val country: String,                                                        //страна
    val trackId: Long,                                                          // id
    val trackName: String,                                                       // Название композиции
    val artistName: String,                                                      // Имя исполнителя
    val trackTime: String,                                                      // Продолжительность трека
    val artworkUrl100: String                                                   // Ссылка на изображение обложки
) : Serializable {
    fun getCoverArtwork() = artworkUrl100.replaceAfterLast('/',"512x512bb.jpg")
    fun getYearFromReleaseDate() = releaseDate.substring(0, 4)
}