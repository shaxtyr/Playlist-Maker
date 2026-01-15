package com.practicum.playlistmaker.old_files_unused

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.old_files_unused.TracksLocalRepository
import com.practicum.playlistmaker.search.data.dto.TrackLocalDto
import com.practicum.playlistmaker.search.data.mapper.TrackLocalMapper
import com.practicum.playlistmaker.search.domain.entity.Track

class TracksLocalRepositoryImpl(context: Context) : TracksLocalRepository {

    private val sharedPreferences = context.getSharedPreferences(TRACK_HISTORY_PREFERENCES,
        Context.MODE_PRIVATE
    )
    private var preferenceChangeListener: SharedPreferences.OnSharedPreferenceChangeListener? = null
    private var changeCallback: (() -> Unit)? = null

    override fun setListener(listener: () -> Unit) {
        changeCallback = listener
        preferenceChangeListener = SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
            if (key == NEW_TRACK_KEY) {
                changeCallback?.invoke()
            }
        }
        preferenceChangeListener?.let { sharedPreferences.registerOnSharedPreferenceChangeListener(it) }
    }

    override fun removeListener() {
        preferenceChangeListener?.let { sharedPreferences.unregisterOnSharedPreferenceChangeListener(it) }
        preferenceChangeListener = null
        changeCallback = null
    }

    private val json = Gson()

    override fun clearHistory() {
        sharedPreferences.edit()
            .clear()
            .apply()
    }

    override fun getHistory() : List<Track> {
        val json = sharedPreferences.getString(NEW_TRACK_KEY, null)
        return if (json != null && json.isNotEmpty()) {
            createTrackListFromJson(json).toList()
        } else {
            emptyList()
        }
    }

    override fun saveHistory(currentHistoryTrackList: List<Track>) {
        val json = createJsonFromTrackList(currentHistoryTrackList as ArrayList<Track>)
        sharedPreferences.edit()
            .putString(NEW_TRACK_KEY, json)
            .apply()
    }

    private fun createJsonFromTrackList(historyTrackList: ArrayList<Track>) : String {
        val historyTrackListDto = historyTrackList.map { TrackLocalMapper.toData(it) }
        return json.toJson(historyTrackListDto)
    }

    private fun createTrackListFromJson(json: String) : List<Track> {

        val arrayOfTrackDto: Array<TrackLocalDto> = Gson().fromJson(json, object : TypeToken<Array<TrackLocalDto>>() {}.type)

        return arrayOfTrackDto.map { TrackLocalMapper.toDomain(it) }
    }

    companion object {
        const val TRACK_HISTORY_PREFERENCES = "track_history_preferences"
        const val NEW_TRACK_KEY = "key_for_new_track"
    }
}