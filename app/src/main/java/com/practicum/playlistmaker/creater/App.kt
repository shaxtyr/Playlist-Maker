package com.practicum.playlistmaker.creater

import android.app.Application

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        Creator.initApplication(this)

        val themeRepository = Creator.getThemeLocalRepository()
        themeRepository.applyTheme()
    }
}