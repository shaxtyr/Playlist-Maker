package com.practicum.playlistmaker.setting.domain.repository

import com.practicum.playlistmaker.setting.domain.model.ThemeSettings

interface SettingsRepository {
    fun getThemeSettings(): ThemeSettings
    fun updateThemeSetting(settings: ThemeSettings)
}