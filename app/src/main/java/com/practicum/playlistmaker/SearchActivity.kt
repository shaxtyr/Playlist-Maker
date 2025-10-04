package com.practicum.playlistmaker

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
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
    private val itunesBaseUrl = "https://itunes.apple.com"
    private val retrofit = Retrofit.Builder()
        .baseUrl(itunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val itunesService = retrofit.create(iTunesApi::class.java)

    private lateinit var backIcon: ImageView
    private lateinit var clearText: ImageView
    private lateinit var editText: EditText
    private lateinit var placeholderImage: ImageView
    private lateinit var placeholderMessage: TextView
    private lateinit var placeholderButton: Button

    private val trackList = ArrayList<Track>()
    private val trackAdapter = TrackAdapter(trackList)



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        backIcon = findViewById(R.id.back_from_settings)
        clearText = findViewById(R.id.button_clear)
        editText = findViewById(R.id.input_edit_text_search)

        placeholderImage = findViewById(R.id.placeholderImage)
        placeholderMessage = findViewById(R.id.placeholderMessage)
        placeholderButton = findViewById(R.id.placeholderButton)

        val recycleView = findViewById<RecyclerView>(R.id.recyclerViewSearch)
        recycleView.layoutManager = LinearLayoutManager(this)
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
            trackList.clear()
            trackAdapter.notifyDataSetChanged()
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

        val isDarkTheme = run {
            val uiMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
            uiMode == Configuration.UI_MODE_NIGHT_YES
        }

        editText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                search(isDarkTheme)
                true
            }
            false
        }

        placeholderButton.setOnClickListener {
            search(isDarkTheme)
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

    private fun search(theme: Boolean) {
        itunesService.search(editText.text.toString())
            .enqueue(object : Callback<TracksResponce> {
                override fun onResponse(
                    call: Call<TracksResponce>,
                    response: Response<TracksResponce>
                ) {
                    when (response.code()) {
                        200 -> {
                            trackList.clear()
                            if (response.body()?.results?.isNotEmpty() == true) {
                                trackList.addAll(response.body()?.results!!)
                                trackAdapter.notifyDataSetChanged()
                            }
                            if (trackList.isEmpty()) {
                                showMessage(getString(R.string.nothing_found), if (theme) R.drawable.ic_nothing_night_120 else R.drawable.ic_nothing_120)
                            } else {
                                showMessage("", 0)
                            }
                        }
                        else -> showMessage(getString(R.string.communication_problems), if (theme) R.drawable.ic_no_connection_night_120 else R.drawable.ic_no_connection_120)
                    }
                }

                override fun onFailure(call: Call<TracksResponce>, t: Throwable) {
                    showMessage(getString(R.string.communication_problems), if (theme) R.drawable.ic_no_connection_night_120 else R.drawable.ic_no_connection_120)
                }
            })
    }

    private fun showMessage(text: String, image: Int) {
        if (text.isNotEmpty()) {

            placeholderImage.visibility = View.VISIBLE
            placeholderMessage.visibility = View.VISIBLE

            if (image == R.drawable.ic_nothing_120 || image == R.drawable.ic_nothing_night_120) {
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
    }
}