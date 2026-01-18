package com.practicum.playlistmaker.search.data.dto

class TrackLocalDto(
    val previewUrl: String,
    val collectionName: String,                                                 //альбом
    val releaseDate: String,                                                    //дата релиза
    val primaryGenreName: String,                                               //жанр
    val country: String,                                                        //страна
    val trackId: Long,                                                          // id
    val trackName: String,                                                       // Название композиции
    val artistName: String,                                                      // Имя исполнителя
    val trackTime: String,                                                      // Продолжительность трека
    val artworkUrl100: String
)