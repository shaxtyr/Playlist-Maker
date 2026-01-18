package com.practicum.playlistmaker.setting.domain

import com.practicum.playlistmaker.setting.domain.interactor.SettingsInteractor
import com.practicum.playlistmaker.setting.domain.model.ThemeSettings
import com.practicum.playlistmaker.setting.domain.repository.SettingsRepository

class SettingsInteractorImpl(private val settingsRepository: SettingsRepository) : SettingsInteractor {

    override fun getThemeSettings(): ThemeSettings {
        return settingsRepository.getThemeSettings()
    }

    override fun updateThemeSetting(settings: ThemeSettings) {
        settingsRepository.updateThemeSetting(settings)
    }
}