package com.practicum.playlistmaker.media.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.practicum.playlistmaker.media.data.dao.TrackAddedToAnyPlaylistDao

@Database(version = 2, entities = [TrackAddedToAnyPlaylistEntity::class])
abstract class TrackAddedToAnyPlaylistDatabase : RoomDatabase() {
    abstract fun trackAddedToAnyPlaylistDao(): TrackAddedToAnyPlaylistDao
}