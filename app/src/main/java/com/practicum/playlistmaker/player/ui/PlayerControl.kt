package com.practicum.playlistmaker.player.ui

import kotlinx.coroutines.flow.StateFlow

interface PlayerControl {
    fun getPlayerStateFlow(): StateFlow<PlayerState>
    fun startPlayer()
    fun pausePlayer()
    fun showNotification()
    fun hideNotification()
}