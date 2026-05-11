package com.practicum.playlistmaker.player.ui

import android.os.Parcel
import android.os.Parcelable
import com.practicum.playlistmaker.media.domain.entity.Playlist
import java.io.Serializable

data class PlayerState(
    val stateMode: EnumStateMode,
    val progressTime: String,
    val isFavorite: Boolean,
    val playlists: List<Playlist> = emptyList(),
    val addedTrackToPlaylistState: AddedTrackToPlaylistState? = null
)