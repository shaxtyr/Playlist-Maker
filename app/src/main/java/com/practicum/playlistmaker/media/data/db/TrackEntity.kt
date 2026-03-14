package com.practicum.playlistmaker.media.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "track_table")
data class TrackEntity(
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
    val previewUrl: String
) : Serializable {
    fun getCoverArtwork() = artworkUrl100.replaceAfterLast('/',"512x512bb.jpg")
    fun getYearFromReleaseDate() = releaseDate.substring(0, 4)
}





