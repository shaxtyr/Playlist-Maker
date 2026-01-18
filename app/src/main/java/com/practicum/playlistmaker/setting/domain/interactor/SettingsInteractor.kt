package com.practicum.playlistmaker.setting.domain.interactor

import com.practicum.playlistmaker.setting.domain.model.ThemeSettings

interface SettingsInteractor {
    fun getThemeSettings(): ThemeSettings
    fun updateThemeSetting(settings: ThemeSettings)
}