package com.practicum.playlistmaker.presentation.searching

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
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
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.creater.Creator
import com.practicum.playlistmaker.domain.interactor.ResponseStatus
import com.practicum.playlistmaker.domain.interactor.StatusException
import com.practicum.playlistmaker.domain.entity.Track
import com.practicum.playlistmaker.domain.interactor.TracksHistoryInteractor
import com.practicum.playlistmaker.domain.interactor.TracksInteractor
import com.practicum.playlistmaker.presentation.audioplayer.AudioPlayerActivity

class SearchTrackActivity : AppCompatActivity() {

    private lateinit var tracksInteractor: TracksInteractor
    private lateinit var historyInteractor: TracksHistoryInteractor
    private var currentText = ""
    private lateinit var backIcon: ImageView
    private lateinit var clearText: ImageView
    private lateinit var clearHistory: Button
    private lateinit var editText: EditText
    private lateinit var placeholderImage: ImageView
    private lateinit var placeholderMessage: TextView
    private lateinit var placeholderButton: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var viewGroupHistoryHint: LinearLayout
    private lateinit var trackAdapter: SearchTrackAdapter
    private lateinit var historyTrackAdapter: SearchTrackAdapter
    private val trackList = ArrayList<Track>()
    private val historyTrackList = ArrayList<Track>()
    private val handler = Handler(Looper.getMainLooper())
    private val searchRunnable = Runnable { search() }
    private var isClickAllowed = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        tracksInteractor = Creator.provideTracksInteractor()
        historyInteractor = Creator.provideTracksHistoryInteractor()

        backIcon = findViewById(R.id.back_from_settings)
        clearText = findViewById(R.id.button_clear)
        editText = findViewById(R.id.input_edit_text_search)
        clearHistory = findViewById(R.id.clear_history)

        progressBar = findViewById(R.id.progressBar)
        placeholderImage = findViewById(R.id.placeholderImage)
        placeholderMessage = findViewById(R.id.placeholderMessage)
        placeholderButton = findViewById(R.id.placeholderButton)
        viewGroupHistoryHint = findViewById(R.id.view_group_history_hint)

        trackAdapter = SearchTrackAdapter(trackList,
            historyInteractor, false, onClickDebounce = { position ->
            if (clickDebounce()) {
                val audioPlayerIntent = Intent(this, AudioPlayerActivity::class.java)
                audioPlayerIntent.putExtra(OPEN_TRACK_KEY, trackList[position])
                startActivity(audioPlayerIntent)
            }
        })
        historyTrackAdapter = SearchTrackAdapter(historyTrackList,
            historyInteractor, true, onClickDebounce = { position ->
                if (clickDebounce()) {
                    val audioPlayerIntent = Intent(this, AudioPlayerActivity::class.java)
                    audioPlayerIntent.putExtra(OPEN_TRACK_KEY, historyTrackList[position])
                    startActivity(audioPlayerIntent)
                }
            })

        updateHistory()
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

        historyInteractor.setListener {
            runOnUiThread {
                historyTrackList.clear()
                historyTrackList.addAll(historyInteractor.getHistory())
                historyTrackAdapter.notifyDataSetChanged()
            }
        }

        backIcon.setOnClickListener {
            finish()
        }

        clearHistory.setOnClickListener {
            historyInteractor.clearHistory()
            historyTrackList.clear()
            historyTrackAdapter.notifyDataSetChanged()
            viewGroupHistoryHint.isVisible = false
        }

        clearText.setOnClickListener {
            editText.setText("")
            editText.clearFocus()
            val inputMethodManager =
                getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(editText.windowToken, 0)
            trackList.clear()
            trackAdapter.notifyDataSetChanged()
        }

        editText.setOnFocusChangeListener { view, hasFocus ->
            viewGroupHistoryHint.isVisible = hasFocus && editText.text.isEmpty() && historyTrackList.isNotEmpty()
        }

        editText.addTextChangedListener(
            onTextChanged = { p0: CharSequence?, p1: Int, p2: Int, p3: Int ->
                clearText.isVisible = !p0.isNullOrEmpty()
                currentText = p0.toString()
                viewGroupHistoryHint.isVisible = editText.hasFocus() && p0?.isEmpty() == true && historyTrackList.isNotEmpty()
                trackList.clear()
                trackAdapter.notifyDataSetChanged()
            },

            afterTextChanged = { p0: Editable? ->
                if (!p0.isNullOrEmpty()) {
                    searchDebounce()
                }
            }
        )

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

    @SuppressLint("SuspiciousIndentation")
    private fun search() {
        progressBar.isVisible = true

            tracksInteractor.searchTracks(
                editText.text.toString(),
                object : TracksInteractor.TracksConsumer {
                    override fun consume(foundTracks: List<Track>) {
                        runOnUiThread {
                            progressBar.isVisible = false
                            if (foundTracks.isNotEmpty()) {
                                trackList.clear()
                                trackList.addAll(foundTracks)
                                trackAdapter.notifyDataSetChanged()
                            }
                            if (trackList.isEmpty()) {
                                viewGroupHistoryHint.isVisible = false
                                showMessage(
                                    getString(R.string.nothing_found),
                                    R.drawable.ic_nothing_120
                                )
                            } else {
                                showMessage("", 0)
                            }
                        }
                    }

                    override fun onError(e: StatusException) {
                        runOnUiThread {
                            progressBar.isVisible = false
                            viewGroupHistoryHint.isVisible = false
                            when (e.status) {
                                ResponseStatus.BAD_REQUEST -> {
                                    progressBar.isVisible = false
                                    viewGroupHistoryHint.isVisible = false
                                    showMessage(
                                        getString(R.string.communication_problems),
                                        R.drawable.ic_no_connection_120
                                    )
                                }

                                ResponseStatus.ERROR -> {
                                    viewGroupHistoryHint.isVisible = false
                                    showMessage(
                                        getString(R.string.communication_problems),
                                        R.drawable.ic_no_connection_120
                                    )
                                }
                            }
                        }
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

    private fun updateHistory() {
        historyTrackList.clear()
        historyTrackList.addAll(historyInteractor.getHistory())
        historyTrackAdapter.notifyDataSetChanged()
    }

    companion object {
        const val OPEN_TRACK_KEY = "open_track"
        const val EDIT_KEY = "EDIT"
        const val EDIT_DEF = ""
        const val CLICK_DEBOUNCE_DELAY = 1000L
        const val SEARCH_DEBOUNCE_DELAY = 2000L
    }
}