package com.practicum.playlistmaker

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {

    private var currentText = ""
    private val retrofit = Retrofit.Builder()
        .baseUrl(ITUNES_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val itunesService = retrofit.create(iTunesApi::class.java)

    private lateinit var backIcon: ImageView
    private lateinit var clearText: ImageView
    private lateinit var clearHistory: Button
    private lateinit var editText: EditText
    private lateinit var placeholderImage: ImageView
    private lateinit var placeholderMessage: TextView
    private lateinit var placeholderButton: Button
    private lateinit var viewGroupHistoryHint: LinearLayout
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var searchHistory: SearchHistory
    private lateinit var trackAdapter: TrackAdapter
    private lateinit var historyTrackAdapter: TrackAdapter
    private lateinit var listener: SharedPreferences.OnSharedPreferenceChangeListener

    private val trackList = ArrayList<Track>()
    private val historyTrackList = ArrayList<Track>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        backIcon = findViewById(R.id.back_from_settings)
        clearText = findViewById(R.id.button_clear)
        editText = findViewById(R.id.input_edit_text_search)
        clearHistory = findViewById(R.id.clear_history)

        placeholderImage = findViewById(R.id.placeholderImage)
        placeholderMessage = findViewById(R.id.placeholderMessage)
        placeholderButton = findViewById(R.id.placeholderButton)
        viewGroupHistoryHint = findViewById(R.id.view_group_history_hint)

        sharedPreferences = getSharedPreferences(TRACK_HISTORY_PREFERENCES, MODE_PRIVATE)
        searchHistory = SearchHistory(sharedPreferences)

        trackAdapter = TrackAdapter(trackList, searchHistory, false)
        historyTrackAdapter = TrackAdapter(historyTrackList, searchHistory, true)

        historyTrackList.clear()
        historyTrackList.addAll(searchHistory.getHistory())
        historyTrackAdapter.notifyDataSetChanged()
        if (historyTrackList.isNotEmpty()) {
            viewGroupHistoryHint.visibility = View.VISIBLE
        } else {
            viewGroupHistoryHint.visibility = View.GONE
        }

        val recycleView = findViewById<RecyclerView>(R.id.recyclerViewSearch)
        recycleView.layoutManager = LinearLayoutManager(this)
        recycleView.adapter = trackAdapter

        val recycleViewHistory = findViewById<RecyclerView>(R.id.recycler_view_history)
        recycleViewHistory.layoutManager = LinearLayoutManager(this)
        recycleViewHistory.adapter = historyTrackAdapter

        listener = SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
            if (key == NEW_TRACK_KEY) {
                historyTrackList.clear()
                historyTrackList.addAll(searchHistory.getHistory())
                historyTrackAdapter.notifyDataSetChanged()
            }
        }
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener)

        backIcon.setOnClickListener {
            finish()
        }

        clearHistory.setOnClickListener {
            sharedPreferences.edit()
                .clear()
                .apply()
            historyTrackList.clear()
            historyTrackAdapter.notifyDataSetChanged()
            viewGroupHistoryHint.visibility = View.GONE
        }

        clearText.setOnClickListener {
            editText.setText("")
            editText.clearFocus()
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(editText.windowToken, 0)
            trackList.clear()
            trackAdapter.notifyDataSetChanged()
        }

        editText.setOnFocusChangeListener { view, hasFocus ->
            viewGroupHistoryHint.visibility = if (hasFocus && editText.text.isEmpty() && historyTrackList.isNotEmpty()) View.VISIBLE else View.GONE
        }

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                clearText.isVisible = !p0.isNullOrEmpty()
                currentText = p0.toString()
                viewGroupHistoryHint.visibility = if (editText.hasFocus() && p0?.isEmpty() == true && historyTrackList.isNotEmpty()) View.VISIBLE else View.GONE
                trackList.clear()
                trackAdapter.notifyDataSetChanged()
            }

            override fun afterTextChanged(p0: Editable?) {}
        }
        editText.addTextChangedListener(simpleTextWatcher)

        editText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                search()
                true
            }
            false
        }

        placeholderButton.setOnClickListener {
            search()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(EDIT_KEY, currentText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        currentText = savedInstanceState.getString(EDIT_KEY, EDIT_DEF)
    }

    private fun search() {
        itunesService.search(editText.text.toString())
            .enqueue(object : Callback<TracksResponce> {
                override fun onResponse(
                    call: Call<TracksResponce>,
                    response: Response<TracksResponce>
                ) {
                    if (response.isSuccessful) {
                        trackList.clear()
                        val responseBodyResults = response.body()?.results
                        if (responseBodyResults?.isNotEmpty() == true) {
                            trackList.addAll(responseBodyResults)
                            trackAdapter.notifyDataSetChanged()
                        }
                        if (trackList.isEmpty()) {
                            viewGroupHistoryHint.visibility = View.GONE
                            showMessage(getString(R.string.nothing_found), R.drawable.ic_nothing_120)
                        } else {
                            showMessage("", 0)
                        }
                    } else {
                        viewGroupHistoryHint.visibility = View.GONE
                        showMessage(getString(R.string.communication_problems), R.drawable.ic_no_connection_120)
                    }
                }

                override fun onFailure(call: Call<TracksResponce>, t: Throwable) {
                    viewGroupHistoryHint.visibility = View.GONE
                    showMessage(getString(R.string.communication_problems), R.drawable.ic_no_connection_120)
                }
            })
    }

    private fun showMessage(text: String, image: Int) {
        if (text.isNotEmpty()) {

            placeholderImage.visibility = View.VISIBLE
            placeholderMessage.visibility = View.VISIBLE

            if (image == R.drawable.ic_nothing_120) {
                placeholderButton.visibility = View.GONE
            } else {
                placeholderButton.visibility = View.VISIBLE
            }

            trackList.clear()
            trackAdapter.notifyDataSetChanged()

            placeholderImage.setImageResource(image)
            placeholderMessage.text = text

        } else {
            placeholderImage.visibility = View.GONE
            placeholderMessage.visibility = View.GONE
            placeholderButton.visibility = View.GONE
        }
    }

    companion object {
        const val EDIT_KEY = "EDIT"
        const val EDIT_DEF = ""
        const val ITUNES_BASE_URL = "https://itunes.apple.com"
        const val TRACK_HISTORY_PREFERENCES = "track_history_preferences"
        const val NEW_TRACK_KEY = "key_for_new_track"
    }
}