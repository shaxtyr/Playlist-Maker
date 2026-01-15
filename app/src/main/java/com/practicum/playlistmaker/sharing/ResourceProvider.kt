package com.practicum.playlistmaker.sharing

interface ResourceProvider {
    fun getString(resourceId: Int): String
}