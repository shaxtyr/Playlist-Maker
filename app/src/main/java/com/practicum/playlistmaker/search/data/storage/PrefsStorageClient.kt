package com.practicum.playlistmaker.search.data.storage

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.practicum.playlistmaker.search.data.StorageClient
import java.lang.reflect.Type

class PrefsStorageClient<T>(
    private val context: Context,
    private val type: Type
) : StorageClient<T> {

    private val prefs: SharedPreferences = context.getSharedPreferences(TRACK_HISTORY_PREFERENCES, Context.MODE_PRIVATE)
    private val gson = Gson()

    override fun storeData(data: T) {
        prefs.edit().putString(NEW_TRACK_KEY, gson.toJson(data, type)).apply()
    }

    override fun getData(): T? {
        val dataJson = prefs.getString(NEW_TRACK_KEY, null)
        if (dataJson == null) {
            return null
        } else {
            return gson.fromJson(dataJson, type)
        }
    }

    companion object {
        const val TRACK_HISTORY_PREFERENCES = "track_history_preferences"
        const val NEW_TRACK_KEY = "key_for_new_track"
    }
}