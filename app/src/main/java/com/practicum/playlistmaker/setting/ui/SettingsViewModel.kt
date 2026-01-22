package com.practicum.playlistmaker.setting.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.setting.domain.interactor.SettingsInteractor
import com.practicum.playlistmaker.setting.domain.model.ThemeSettings
import com.practicum.playlistmaker.sharing.domain.interactor.SharingInteractor

class SettingsViewModel (private val sharingInteractor: SharingInteractor, private val settingsInteractor: SettingsInteractor) : ViewModel() {

    private val themeSettingsLiveData = MutableLiveData<ThemeSettings>()
    fun observeThemeSettings(): LiveData<ThemeSettings> = themeSettingsLiveData

    init {
        themeSettingsLiveData.postValue(settingsInteractor.getThemeSettings())
    }

    fun switchTheme(fromSwitch: Boolean) {

        settingsInteractor.updateThemeSetting(ThemeSettings(fromSwitch))
        val newSettings = settingsInteractor.getThemeSettings()

        themeSettingsLiveData.postValue(newSettings)

    }
    fun writeToSupport() {
        sharingInteractor.openSupport()
    }

    fun shareApp() {
        sharingInteractor.shareApp()
    }

    fun showUserDoc() {
        sharingInteractor.openTerms()
    }
}