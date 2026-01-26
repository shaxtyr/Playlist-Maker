package com.practicum.playlistmaker.creater

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.media.MediaPlayer
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.search.data.network.RetrofitNetworkClient
import com.practicum.playlistmaker.search.data.network.iTunesApi
import com.practicum.playlistmaker.search.data.repository.TracksNetRepositoryImpl
import com.practicum.playlistmaker.search.domain.TracksInteractorImpl
import com.practicum.playlistmaker.search.domain.entity.Track
import com.practicum.playlistmaker.search.domain.interactor.TracksInteractor
import com.practicum.playlistmaker.search.domain.repository.TracksNetRepository
import com.practicum.playlistmaker.search.data.repository.SearchHistoryRepositoryImpl
import com.practicum.playlistmaker.search.data.storage.PrefsStorageClient
import com.practicum.playlistmaker.search.data.storage.PrefsStorageClient.Companion.TRACK_HISTORY_PREFERENCES
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
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

//не используется
object Creator {
    private lateinit var application: Application
    private lateinit var tracksInteractor: TracksInteractor
    private lateinit var searchHistoryInteractor: SearchHistoryInteractor
    private lateinit var settingsInteractor: SettingsInteractor
    private lateinit var sharingInteractor: SharingInteractor
    private lateinit var prefs: SharedPreferences
    private lateinit var gson: Gson
    private lateinit var iTunesBaseUrl: String
    private lateinit var retrofit: Retrofit
    private lateinit var iTunesService: iTunesApi

    fun initApplication(application: Application) {

        iTunesBaseUrl = "https://itunes.apple.com"

        retrofit = Retrofit.Builder()
            .baseUrl(iTunesBaseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        iTunesService = retrofit.create(iTunesApi::class.java)

        prefs = application.getSharedPreferences(TRACK_HISTORY_PREFERENCES, Context.MODE_PRIVATE)
        gson = Gson()

        this.application = application
        tracksInteractor = provideTracksInteractor()
        searchHistoryInteractor = provideSearchHistoryInteractor()
        settingsInteractor = provideSettingsInteractor()
        sharingInteractor = provideSharingInteractor()

    }

    fun getMediaPlayer(): MediaPlayer {
        return MediaPlayer()
    }

    // for Retrofit
    private fun getTracksNetRepository(): TracksNetRepository {
        return TracksNetRepositoryImpl(RetrofitNetworkClient(application, iTunesService))
    }

    fun provideTracksInteractor(): TracksInteractor {
        return TracksInteractorImpl(getTracksNetRepository())
    }

    // for History
    private fun getSearchHistoryRepository(): SearchHistoryRepository {
        return SearchHistoryRepositoryImpl(
            PrefsStorageClient<ArrayList<Track>>(
                object : TypeToken<ArrayList<Track>>() {}.type,
                prefs,
                gson
            )
        )
    }

    fun provideSearchHistoryInteractor(): SearchHistoryInteractor {
        return SearchHistoryInteractorImpl(getSearchHistoryRepository())
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