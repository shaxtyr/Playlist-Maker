package com.practicum.playlistmaker

import android.content.SharedPreferences
import com.google.gson.Gson

class SearchHistory(val sharedPreferences: SharedPreferences) {

    private val json = Gson()

    fun clearHistory() {
        sharedPreferences.edit()
            .clear()
            .apply()
    }

    fun addToHistory(track: Track) {
        val currentHistoryTrackList = getHistory().toMutableList()

        if (currentHistoryTrackList.any {it.trackId == track.trackId}) {
            currentHistoryTrackList.remove(track)
        }
        if (currentHistoryTrackList.size >= 10) {
            currentHistoryTrackList.removeAt(currentHistoryTrackList.size - 1)
        }
        currentHistoryTrackList.add(0, track)

        saveHistory(currentHistoryTrackList)
    }

    fun getHistory(): List<Track> {
        val json = sharedPreferences.getString(NEW_TRACK_KEY, null)
        return if (json != null && json.isNotEmpty()) {
            createTrackListFromJson(json).toList()
        } else {
            emptyList()
        }
    }

    private fun saveHistory(currentHistoryTrackList: List<Track>) {
        val json = createJsonFromTrackList(currentHistoryTrackList as ArrayList<Track>)
        sharedPreferences.edit()
            .putString(NEW_TRACK_KEY, json)
            .apply()
    }

    private fun createJsonFromTrackList(historyTrackList: ArrayList<Track>): String {
        return json.toJson(historyTrackList)
    }

    private fun createTrackListFromJson(json: String): Array<Track> {
        return Gson().fromJson(json, Array<Track>::class.java)
    }

    companion object {
        const val NEW_TRACK_KEY = "key_for_new_track"
    }
}