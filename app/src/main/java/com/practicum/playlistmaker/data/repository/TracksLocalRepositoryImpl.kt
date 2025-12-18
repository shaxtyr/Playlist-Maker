package com.practicum.playlistmaker.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

import com.practicum.playlistmaker.data.dto.TrackLocalDto
import com.practicum.playlistmaker.data.mapper.TrackLocalMapper
import com.practicum.playlistmaker.domain.entity.Track
import com.practicum.playlistmaker.domain.repository.TracksLocalRepository

class TracksLocalRepositoryImpl(context: Context) : TracksLocalRepository {

    private val sharedPreferences = context.getSharedPreferences(TRACK_HISTORY_PREFERENCES,
        Context.MODE_PRIVATE
    )
    private var preferenceChangeListener: SharedPreferences.OnSharedPreferenceChangeListener? = null
    private var changeCallback: (() -> Unit)? = null

    override fun setListener(listener: () -> Unit) {
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

    override fun addToHistory(track: Track) {

        val currentHistoryTrackList = getHistory().toMutableList()

        val trackLocalDto = TrackLocalMapper.toData(track)

        val itemToRemove = currentHistoryTrackList.find { it.trackId == trackLocalDto.trackId}
        if (itemToRemove != null) {
            currentHistoryTrackList.remove(itemToRemove)
        }

        /*if (currentHistoryTrackList.any {it.trackId == trackLocalDto.trackId}) {
            currentHistoryTrackList.remove(trackLocalDto)
        }*/

        if (currentHistoryTrackList.size >= 10) {
            currentHistoryTrackList.removeAt(currentHistoryTrackList.size - 1)
        }
        currentHistoryTrackList.add(0, track)

        saveHistory(currentHistoryTrackList)
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

    override fun createJsonFromTrackList(historyTrackList: ArrayList<Track>) : String {
        val historyTrackListDto = historyTrackList.map { TrackLocalMapper.toData(it) }
        return json.toJson(historyTrackListDto)
    }

    override fun createTrackListFromJson(json: String) : List<Track> {

        val arrayOfTrackDto: Array<TrackLocalDto> = Gson().fromJson(json, object : TypeToken<Array<TrackLocalDto>>() {}.type)

        return arrayOfTrackDto.map { TrackLocalMapper.toDomain(it) }
    }

    companion object {
        const val TRACK_HISTORY_PREFERENCES = "track_history_preferences"
        const val NEW_TRACK_KEY = "key_for_new_track"
    }
}