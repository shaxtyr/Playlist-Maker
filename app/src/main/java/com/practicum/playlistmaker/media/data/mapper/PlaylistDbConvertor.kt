package com.practicum.playlistmaker.media.data.mapper

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.media.data.db.PlaylistEntity
import com.practicum.playlistmaker.media.data.dto.PlaylistDto
import com.practicum.playlistmaker.media.domain.entity.Playlist

class PlaylistDbConvertor(private val gson: Gson) {

    fun toDomain(playlistDto: PlaylistDto): Playlist {
        return Playlist(
            playlistDto.playlistId,
            playlistDto.playlistName,
            playlistDto.playlistDescription,
            playlistDto.imagePath,
            playlistDto.listIdTracks,
            playlistDto.numberOfTracks
        )
    }

    fun toData(playlist: Playlist): PlaylistDto {
        return PlaylistDto(
            playlist.playlistId,
            playlist.playlistName,
            playlist.playlistDescription,
            playlist.imagePath,
            playlist.listIdTracks,
            playlist.numberOfTracks
        )
    }

    fun map(playlistDto: PlaylistDto): PlaylistEntity {
        return PlaylistEntity(
            playlistDto.playlistId,
            playlistDto.playlistName,
            playlistDto.playlistDescription,
            playlistDto.imagePath,
            convertToJson(playlistDto.listIdTracks),
            playlistDto.numberOfTracks
            )
    }

    fun map(playlistEntity: PlaylistEntity): PlaylistDto {
        return PlaylistDto(
            playlistEntity.playlistId,
            playlistEntity.playlistName,
            playlistEntity.playlistDescription,
            playlistEntity.imagePath,
            parseJson(playlistEntity.listIdTracks),
            playlistEntity.numberOfTracks
            )
    }

    fun convertToJson(data: List<Int>): String {
        return gson.toJson(data)
    }

    fun parseJson(jsonString: String): List<Int> {
        val type = object : TypeToken<List<Int>>() {}.type
        return gson.fromJson(jsonString, type)
    }

}