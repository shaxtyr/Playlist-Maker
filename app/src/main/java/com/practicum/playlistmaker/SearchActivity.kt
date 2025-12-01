package com.practicum.playlistmaker

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
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
    private lateinit var progressBar: ProgressBar
    private lateinit var viewGroupHistoryHint: LinearLayout
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var searchHistory: SearchHistory
    private lateinit var trackAdapter: TrackAdapter
    private lateinit var historyTrackAdapter: TrackAdapter
    private lateinit var listener: SharedPreferences.OnSharedPreferenceChangeListener

    private val trackList = ArrayList<Track>()
    private val historyTrackList = ArrayList<Track>()

    private val handler = Handler(Looper.getMainLooper())
    private val searchRunnable = Runnable { search() }
    private var isClickAllowed = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        backIcon = findViewById(R.id.back_from_settings)
        clearText = findViewById(R.id.button_clear)
        editText = findViewById(R.id.input_edit_text_search)
        clearHistory = findViewById(R.id.clear_history)

        progressBar = findViewById(R.id.progressBar)
        placeholderImage = findViewById(R.id.placeholderImage)
        placeholderMessage = findViewById(R.id.placeholderMessage)
        placeholderButton = findViewById(R.id.placeholderButton)
        viewGroupHistoryHint = findViewById(R.id.view_group_history_hint)

        sharedPreferences = getSharedPreferences(TRACK_HISTORY_PREFERENCES, MODE_PRIVATE)
        searchHistory = SearchHistory(sharedPreferences)

        trackAdapter = TrackAdapter(trackList, searchHistory, false, onClickDebounce = { position ->
           if (clickDebounce()) {
               val audioPlayerIntent = Intent(this, AudioPlayerActivity::class.java)
               audioPlayerIntent.putExtra(OPEN_TRACK_KEY, trackList[position])
               startActivity(audioPlayerIntent)
           }
        })
        historyTrackAdapter = TrackAdapter(historyTrackList, searchHistory, true, onClickDebounce = { position ->
            if (clickDebounce()) {
                val audioPlayerIntent = Intent(this, AudioPlayerActivity::class.java)
                audioPlayerIntent.putExtra(OPEN_TRACK_KEY, historyTrackList[position])
                startActivity(audioPlayerIntent)
            }
        })

        historyTrackList.clear()
        historyTrackList.addAll(searchHistory.getHistory())
        historyTrackAdapter.notifyDataSetChanged()
        if (historyTrackList.isNotEmpty()) {
            viewGroupHistoryHint.isVisible = true
        } else {
            viewGroupHistoryHint.isVisible = false
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
            viewGroupHistoryHint.isVisible = false
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
            viewGroupHistoryHint.isVisible = hasFocus && editText.text.isEmpty() && historyTrackList.isNotEmpty()
        }

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                clearText.isVisible = !p0.isNullOrEmpty()
                currentText = p0.toString()
                viewGroupHistoryHint.isVisible = editText.hasFocus() && p0?.isEmpty() == true && historyTrackList.isNotEmpty()
                trackList.clear()
                trackAdapter.notifyDataSetChanged()
            }

            override fun afterTextChanged(p0: Editable?) {
                if (!p0.isNullOrEmpty()) {
                    searchDebounce()
                }
            }
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

        progressBar.isVisible = true

        itunesService.search(editText.text.toString())
            .enqueue(object : Callback<TracksResponce> {
                override fun onResponse(
                    call: Call<TracksResponce>,
                    response: Response<TracksResponce>
                ) {
                    progressBar.isVisible = false
                    if (response.isSuccessful) {
                        trackList.clear()
                        val responseBodyResults = response.body()?.results
                        if (responseBodyResults?.isNotEmpty() == true) {
                            trackList.addAll(responseBodyResults)
                            trackAdapter.notifyDataSetChanged()
                        }
                        if (trackList.isEmpty()) {
                            viewGroupHistoryHint.isVisible = false
                            showMessage(getString(R.string.nothing_found), R.drawable.ic_nothing_120)
                        } else {
                            showMessage("", 0)
                        }
                    } else {
                        viewGroupHistoryHint.isVisible = false
                        showMessage(getString(R.string.communication_problems), R.drawable.ic_no_connection_120)
                    }
                }

                override fun onFailure(call: Call<TracksResponce>, t: Throwable) {
                    progressBar.isVisible = false
                    viewGroupHistoryHint.isVisible = false
                    showMessage(getString(R.string.communication_problems), R.drawable.ic_no_connection_120)
                }
            })
    }

    private fun showMessage(text: String, image: Int) {
        if (text.isNotEmpty()) {

            placeholderImage.isVisible = true
            placeholderMessage.isVisible = true

            if (image == R.drawable.ic_nothing_120) {
                placeholderButton.isVisible = false
            } else {
                placeholderButton.isVisible = true
            }

            trackList.clear()
            trackAdapter.notifyDataSetChanged()

            placeholderImage.setImageResource(image)
            placeholderMessage.text = text

        } else {
            placeholderImage.isVisible = false
            placeholderMessage.isVisible = false
            placeholderButton.isVisible = false
        }
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true },CLICK_DEBOUNCE_DELAY)
        }
        return current
    }
    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    companion object {
        const val OPEN_TRACK_KEY = "open_track"
        const val EDIT_KEY = "EDIT"
        const val EDIT_DEF = ""
        const val ITUNES_BASE_URL = "https://itunes.apple.com"
        const val TRACK_HISTORY_PREFERENCES = "track_history_preferences"
        const val NEW_TRACK_KEY = "key_for_new_track"
        const val CLICK_DEBOUNCE_DELAY = 1000L
        const val  SEARCH_DEBOUNCE_DELAY = 2000L
    }
}