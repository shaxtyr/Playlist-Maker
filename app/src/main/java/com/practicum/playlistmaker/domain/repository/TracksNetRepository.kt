package com.practicum.playlistmaker.domain.repository

import com.practicum.playlistmaker.domain.entity.Track

interface TracksNetRepository {
    fun searchTracks(term: String): List<Track>
}