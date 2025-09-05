package com.practicum.playlistmaker

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SearchActivity : AppCompatActivity() {

    private var currentText = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val backIcon = findViewById<ImageView>(R.id.back_from_settings)
        val clearText = findViewById<ImageView>(R.id.button_clear)
        val editText = findViewById<EditText>(R.id.input_edit_text_search)

        backIcon.setOnClickListener {
            val backIntent = Intent(this@SearchActivity, MainActivity::class.java)
            startActivity(backIntent)
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
                clearText.visibility = clearButtonVisibility(p0)
                currentText = p0.toString()
            }

            override fun afterTextChanged(p0: Editable?) {}
        }
        editText.addTextChangedListener(simpleTextWatcher)
    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
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

    companion object {
        const val EDIT_KEY = "EDIT"
        const val EDIT_DEF = ""
    }
}