package com.practicum.playlistmaker.media.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "trackAddedToAnyPlaylist_table")
data class TrackAddedToAnyPlaylistEntity(
    @PrimaryKey
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
    val timestamp: Long = System.currentTimeMillis()
)
