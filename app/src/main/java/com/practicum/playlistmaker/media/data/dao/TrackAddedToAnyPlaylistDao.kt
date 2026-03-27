package com.practicum.playlistmaker.media.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.practicum.playlistmaker.media.data.db.TrackAddedToAnyPlaylistEntity

@Dao
interface TrackAddedToAnyPlaylistDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTrackAddedToAnyPlaylistEntity(trackAddedToAnyPlaylistEntity: TrackAddedToAnyPlaylistEntity)

}
