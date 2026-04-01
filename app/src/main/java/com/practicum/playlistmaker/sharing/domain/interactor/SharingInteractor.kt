package com.practicum.playlistmaker.sharing.domain.interactor

interface SharingInteractor {
    fun shareApp()
    fun openTerms()
    fun openSupport()
    fun sharePlaylist(message: String)
}