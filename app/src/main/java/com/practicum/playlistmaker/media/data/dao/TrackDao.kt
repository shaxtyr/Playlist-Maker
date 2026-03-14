package com.practicum.playlistmaker.media.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.practicum.playlistmaker.media.data.db.TrackEntity

@Dao
interface TrackDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrackEntity(trackEntity: TrackEntity)

    @Delete
    suspend fun deleteTrackEntity(trackEntity: TrackEntity)

    @Query("SELECT * FROM track_table")
    suspend fun getTrackEntities(): List<TrackEntity>

    @Query("SELECT trackId From track_table")
    suspend fun getListIdTrackEntities(): List<Long>

}