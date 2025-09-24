package com.practicum.playlistmaker

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SearchActivity : AppCompatActivity() {

    private var currentText = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)


        val trackList = listOf<Track>(
            Track(
                getString(R.string.track_name_track1),
                getString(R.string.artist_name_track1),
                getString(R.string.track_time_track1),
                getString(R.string.artwork_url_track1)),
            Track(
                getString(R.string.track_name_track2),
                getString(R.string.artist_name_track2),
                getString(R.string.track_time_track2),
                getString(R.string.artwork_url_track2)),
            Track(
                getString(R.string.track_name_track3),
                getString(R.string.artist_name_track3),
                getString(R.string.track_time_track3),
                getString(R.string.artwork_url_track3)),
            Track(
                getString(R.string.track_name_track4),
                getString(R.string.artist_name_track4),
                getString(R.string.track_time_track4),
                getString(R.string.artwork_url_track4)),
            Track(
                getString(R.string.track_name_track5),
                getString(R.string.artist_name_track5),
                getString(R.string.track_time_track5),
                getString(R.string.artwork_url_track5))
        )

        val backIcon = findViewById<ImageView>(R.id.back_from_settings)
        val clearText = findViewById<ImageView>(R.id.button_clear)
        val editText = findViewById<EditText>(R.id.input_edit_text_search)

        val recycleView = findViewById<RecyclerView>(R.id.recyclerViewSearch)
        recycleView.layoutManager = LinearLayoutManager(this)
        val trackAdapter = TrackAdapter(trackList)
        recycleView.adapter = trackAdapter

        backIcon.setOnClickListener {
            finish()
        }

        clearText.setOnClickListener {
            editText.setText("")
            editText.clearFocus()
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(editText.windowToken, 0)
        }

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                clearText.isVisible = !p0.isNullOrEmpty()
                currentText = p0.toString()
            }

            override fun afterTextChanged(p0: Editable?) {}
        }
        editText.addTextChangedListener(simpleTextWatcher)

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(EDIT_KEY, currentText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        currentText = savedInstanceState.getString(EDIT_KEY, EDIT_DEF)
    }

    companion object {
        const val EDIT_KEY = "EDIT"
        const val EDIT_DEF = ""
    }
}