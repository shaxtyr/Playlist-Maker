package com.practicum.playlistmaker.search.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.practicum.playlistmaker.R
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.practicum.playlistmaker.databinding.ActivitySearchBinding
import com.practicum.playlistmaker.search.domain.entity.Track
import com.practicum.playlistmaker.player.ui.PlayerActivity

class SearchTrackActivity : AppCompatActivity() {
    private var currentText = ""
    private lateinit var communicationProblemMessage: String
    private lateinit var emptyListMessage: String
    private lateinit var binding: ActivitySearchBinding
    private var isClickAllowed = true
    private val viewModel by viewModel<SearchTrackViewModel>()
    private var handler: Handler? = null


    private val tracksAdapter = TracksAdapter {
        if (clickDebounce()) {
            val audioPlayerIntent = Intent(this, PlayerActivity::class.java)
            audioPlayerIntent.putExtra(OPEN_TRACK_KEY, it)
            addTrackToHistory(it)
            startActivity(audioPlayerIntent)
        }
    }

    private val tracksHistoryAdapter = TracksAdapter {
        if (clickDebounce()) {
            val audioPlayerIntent = Intent(this, PlayerActivity::class.java)
            audioPlayerIntent.putExtra(OPEN_TRACK_KEY, it)
            startActivity(audioPlayerIntent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        communicationProblemMessage = getString(R.string.communication_problems)
        emptyListMessage = getString(R.string.nothing_found)


        viewModel.observeTracksState().observe(this) {
            render(it)
        }

        binding.recyclerViewSearch.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewSearch.adapter = tracksAdapter

        binding.recyclerViewHistory.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewHistory.adapter = tracksHistoryAdapter

        viewModel.loadHistory()

        binding.backFromSettings.setOnClickListener {
            finish()
        }

        binding.clearHistory.setOnClickListener {
            viewModel.clearHistory()
            binding.viewGroupHistoryHint.isVisible = false
            tracksHistoryAdapter.notifyDataSetChanged()
        }

        binding.buttonClear.setOnClickListener {
            binding.inputEditTextSearch.setText("")
            binding.inputEditTextSearch.clearFocus()
            val inputMethodManager =
                getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(binding.inputEditTextSearch.windowToken, 0)
            tracksHistoryAdapter.notifyDataSetChanged()
        }

        binding.inputEditTextSearch.setOnFocusChangeListener { view, hasFocus ->

            if (hasFocus && binding.inputEditTextSearch.text.isEmpty()) {
                binding.viewGroupHistoryHint.isVisible = tracksHistoryAdapter.tracks.isNotEmpty()
            } else {
                binding.viewGroupHistoryHint.isVisible = false
                showContent(tracksAdapter.tracks)
            }
        }

        binding.inputEditTextSearch.addTextChangedListener(
            onTextChanged = { p0: CharSequence?, p1: Int, p2: Int, p3: Int ->
                binding.buttonClear.isVisible = !p0.isNullOrEmpty()
                currentText = p0.toString()
                binding.viewGroupHistoryHint.isVisible = binding.inputEditTextSearch.hasFocus() && p0?.isEmpty() == true && tracksHistoryAdapter.tracks.isNotEmpty()
            },

            afterTextChanged = { p0: Editable? ->
                if (!p0.isNullOrEmpty()) {
                    viewModel.searchDebounce(p0.toString(), communicationProblemMessage, emptyListMessage)
                }
            }
        )

        binding.inputEditTextSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                viewModel.search(binding.inputEditTextSearch.text.toString(), communicationProblemMessage, emptyListMessage)
                true
            }
            false
        }

        binding.placeholderButton.setOnClickListener {
            viewModel.search(binding.inputEditTextSearch.text.toString(), communicationProblemMessage, emptyListMessage)
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

    private fun clickDebounce(): Boolean {
        handler = Handler(Looper.getMainLooper())
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler?.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }
    fun showLoading() {
        binding.apply {
            recyclerViewSearch.isVisible = false
            placeholderImage.isVisible = false
            placeholderButton.isVisible = false
            placeholderMessage.isVisible = false
            progressBar.isVisible = true
        }
    }

    fun showEmpty(message: String) {
        binding.apply {
            recyclerViewSearch.isVisible = false

            placeholderImage.apply {
                setImageResource(R.drawable.ic_nothing_120)
                isVisible = true
            }

            placeholderButton.isVisible = false
            placeholderMessage.isVisible = true
            progressBar.isVisible = false

            placeholderMessage.text = message
        }
    }

    fun showError(errorMessage: String) {
        binding.apply {
            recyclerViewSearch.isVisible = false

            placeholderImage.apply {
                setImageResource(R.drawable.ic_no_connection_120)
                isVisible = true
            }

            placeholderButton.isVisible = true
            placeholderMessage.isVisible = true
            progressBar.isVisible = false

            placeholderMessage.text = errorMessage
        }
    }

    fun showContent(tracks: List<Track>) {
        binding.apply {
            recyclerViewSearch.isVisible = true
            placeholderImage.isVisible = false
            placeholderButton.isVisible = false
            placeholderMessage.isVisible = false
            progressBar.isVisible = false
        }

        tracksAdapter.tracks.clear()
        tracksAdapter.tracks.addAll(tracks)
        tracksAdapter.notifyDataSetChanged()
    }

    fun showHistoryContent(tracksHistory: List<Track>) {
        binding.viewGroupHistoryHint.isVisible = tracksHistory.isNotEmpty() && !binding.inputEditTextSearch.hasFocus() && binding.inputEditTextSearch.text.isEmpty()
        tracksHistoryAdapter.tracks.clear()
        tracksHistoryAdapter.tracks.addAll(tracksHistory)
        tracksHistoryAdapter.notifyDataSetChanged()
    }

    fun addTrackToHistory(track: Track) {
        viewModel.addTrackToHistory(track)
    }

    fun render(state: TracksState) {
        when (state) {
            is TracksState.Loading -> showLoading()
            is TracksState.Error -> showError(state.errorMessage)
            is TracksState.Empty -> showEmpty(state.message)
            is TracksState.Content -> showContent(state.tracks)
            is TracksState.ContentHistory -> showHistoryContent(state.tracksHistory)
        }
    }

    companion object {
        const val OPEN_TRACK_KEY = "open_track"
        const val EDIT_KEY = "EDIT"
        const val EDIT_DEF = ""
        const val CLICK_DEBOUNCE_DELAY = 1000L
    }
}