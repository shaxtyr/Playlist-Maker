package com.practicum.playlistmaker.creater

import android.app.Application
import android.content.Context
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.search.data.network.RetrofitNetworkClient
import com.practicum.playlistmaker.search.data.repository.TracksNetRepositoryImpl
import com.practicum.playlistmaker.search.domain.TracksInteractorImpl
import com.practicum.playlistmaker.search.domain.entity.Track
import com.practicum.playlistmaker.search.domain.interactor.TracksInteractor
import com.practicum.playlistmaker.search.domain.repository.TracksNetRepository
import com.practicum.playlistmaker.search.data.repository.SearchHistoryRepositoryImpl
import com.practicum.playlistmaker.search.data.storage.PrefsStorageClient
import com.practicum.playlistmaker.search.domain.SearchHistoryInteractorImpl
import com.practicum.playlistmaker.search.domain.interactor.SearchHistoryInteractor
import com.practicum.playlistmaker.search.domain.repository.SearchHistoryRepository
import com.practicum.playlistmaker.setting.data.repository.SettingsRepositoryImpl
import com.practicum.playlistmaker.setting.domain.SettingsInteractorImpl
import com.practicum.playlistmaker.setting.domain.interactor.SettingsInteractor
import com.practicum.playlistmaker.setting.domain.repository.SettingsRepository
import com.practicum.playlistmaker.sharing.ExternalNavigator
import com.practicum.playlistmaker.sharing.ResourceProvider
import com.practicum.playlistmaker.sharing.SharingInteractorImpl
import com.practicum.playlistmaker.sharing.data.repository.AndroidResourceProvider
import com.practicum.playlistmaker.sharing.data.repository.ExternalNavigatorImpl
import com.practicum.playlistmaker.sharing.domain.interactor.SharingInteractor

object Creator {

    private lateinit var application: Application

    fun initApplication(application: Application) {
        this.application = application
    }

    // for Retrofit
    private fun getTracksNetRepository(context: Context): TracksNetRepository {
        return TracksNetRepositoryImpl(RetrofitNetworkClient(context))
    }

    fun provideTracksInteractor(context: Context): TracksInteractor {
        return TracksInteractorImpl(getTracksNetRepository(context))
    }

    // for History
    private fun getSearchHistoryRepository(context: Context): SearchHistoryRepository {
        return SearchHistoryRepositoryImpl(
            PrefsStorageClient<ArrayList<Track>>(
                context,
                object : TypeToken<ArrayList<Track>>() {}.type
            )
        )
    }

    fun provideSearchHistoryInteractor(context: Context): SearchHistoryInteractor {
        return SearchHistoryInteractorImpl(getSearchHistoryRepository(context))
    }


    // for interactor
    fun getSettingsRepository(): SettingsRepository {
        return SettingsRepositoryImpl(context = application)
    }

    fun provideSettingsInteractor(): SettingsInteractor {
        return SettingsInteractorImpl(getSettingsRepository())
    }


    // for sharing
    fun getExternalNavigator(): ExternalNavigator {
        return ExternalNavigatorImpl(context = application)
    }

    fun getResourceProvider(): ResourceProvider {
        return AndroidResourceProvider(context = application)
    }

    fun provideSharingInteractor(): SharingInteractor {
        return SharingInteractorImpl(getExternalNavigator(), getResourceProvider())
    }
}