package com.practicum.playlistmaker.media.data.dto

data class TrackAddedToAnyPlaylistDto(
    val trackId: Long,                                                          // id
    val artworkUrl100: String,
    val trackName: String,                                                       // Название композиции
    val artistName: String,                                                      // Имя исполнителя
    val collectionName: String,                                                 //альбом
    val releaseDate: String,                                                    //дата релиза
    val primaryGenreName: String,                                               //жанр
    val country: String,                                                        //страна
    val trackTime: String,                                                      // Продолжительность трека
    val previewUrl: String,
)
