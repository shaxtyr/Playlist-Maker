package com.practicum.playlistmaker.media.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.practicum.playlistmaker.media.data.dao.TrackDao

@Database(version = 1, entities = [TrackEntity::class])
abstract class TrackDatabase : RoomDatabase() {

    abstract fun trackDao(): TrackDao
}