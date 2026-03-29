package com.practicum.playlistmaker.media.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.practicum.playlistmaker.media.data.db.PlaylistEntity
import com.practicum.playlistmaker.media.data.db.TrackAddedToAnyPlaylistEntity

@Dao
interface TrackAddedToAnyPlaylistDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTrackAddedToAnyPlaylistEntity(trackAddedToAnyPlaylistEntity: TrackAddedToAnyPlaylistEntity)

    @Query("SELECT * FROM trackAddedToAnyPlaylist_table WHERE trackId IN (:listIdTracks)")
    suspend fun getAddedTracks(listIdTracks: List<Int>): List<TrackAddedToAnyPlaylistEntity>

    //@Query("SELECT * FROM trackAddedToAnyPlaylist_table")
    //suspend fun getAddedTracks(): List<TrackAddedToAnyPlaylistEntity>
}
