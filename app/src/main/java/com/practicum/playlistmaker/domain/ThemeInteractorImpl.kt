package com.practicum.playlistmaker.domain

import com.practicum.playlistmaker.domain.interactor.ThemeInteractor
import com.practicum.playlistmaker.domain.repository.ThemeLocalRepository

class ThemeInteractorImpl(private val repository: ThemeLocalRepository) : ThemeInteractor {
    override fun switchTheme() {
        val newTheme: Boolean
        val currentTheme = repository.getTheme()
        newTheme = !currentTheme
        repository.saveTheme(newTheme)
        repository.applyTheme()
    }
}