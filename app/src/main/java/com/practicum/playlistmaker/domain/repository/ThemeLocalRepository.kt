package com.practicum.playlistmaker.domain.repository

interface ThemeLocalRepository {
    fun getTheme() : Boolean
    fun applyTheme()
    fun saveTheme(theme: Boolean)
}